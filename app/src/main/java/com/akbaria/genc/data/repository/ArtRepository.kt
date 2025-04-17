package com.akbaria.genc.data.repository

import android.net.Uri
import android.util.Log
import com.akbaria.genc.domain.model.Artwork
import com.akbaria.genc.domain.model.User
import com.akbaria.genc.domain.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val realtimeDb = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val TAG = "ArtRepository"

    // Şəkil yükləmək üçün yeni metod
    suspend fun uploadImage(imageUri: Uri, artworkId: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            Log.d(TAG, "Starting image upload for artwork: $artworkId")

            val storageRef = storage.reference.child("artworks/${artworkId}.jpg")
            val uploadTask = storageRef.putFile(imageUri).await()
            Log.d(TAG, "Image uploaded successfully, getting download URL")

            // Get download URL (don't add ?alt=media parameter)
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Log.d(TAG, "Download URL retrieved: $downloadUrl")

            // Update the artwork with the image URL
            realtimeDb.reference.child("artworks").child(artworkId)
                .child("imageUrl")
                .setValue(downloadUrl)
                .await()
            Log.d(TAG, "Artwork updated with image URL in database")

            emit(Resource.Success(downloadUrl))
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading image: ${e.message}", e)
            emit(Resource.Error("Resim yüklenemedi: ${e.message}"))
        }
    }

    // Satıcının yeni məhsul əlavə etməsi (Realtime Database istifadə edərək)
    suspend fun addArtwork(artwork: Artwork): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {
            Log.d(TAG, "Adding new artwork")

            // Cari istifadəçi ID-si
            val userId = auth.currentUser?.uid ?: throw Exception("İstifadəçi girişi yoxdur")
            Log.d(TAG, "Current userId: $userId")

            // Rəsm üçün yeni ID yaradırıq
            val artworkRef = realtimeDb.reference.child("artworks").push()
            val artworkId = artworkRef.key ?: throw Exception("ID yaratmaq mümkün olmadı")
            Log.d(TAG, "Generated artworkId: $artworkId")

            // Satıcı məlumatlarını alırıq
            val sellerSnapshot = firestore.collection("users").document(userId).get().await()
            val sellerName = sellerSnapshot.getString("name") ?: "Anonim Rəssam"
            Log.d(TAG, "Seller name: $sellerName")

            // Məlumatları Realtime Database-ə əlavə edirik
            val artworkWithDetails = artwork.copy(
                id = artworkId,
                sellerId = userId,
                sellerName = sellerName,
                imageUrl = "",  // Boş şəkil URL, daha sonra yeniləyəcəyik
                createdAt = System.currentTimeMillis()
            )

            artworkRef.setValue(artworkWithDetails).await()
            Log.d(TAG, "Artwork added to database successfully")

            // Müvəffəqiyyətlə tamamlandı
            emit(Resource.Success(artworkId))

        } catch (e: Exception) {
            Log.e(TAG, "Error adding artwork: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "Bir xəta baş verdi"))
        }
    }

    // Satıcının öz əsərlərini əldə etməsi (Realtime Database istifadə edərək)
    fun getSellerArtworks(): Flow<Resource<List<Artwork>>> = flow {
        emit(Resource.Loading())

        try {
            Log.d(TAG, "Getting seller artworks")

            val userId = auth.currentUser?.uid ?: throw Exception("İstifadəçi girişi yoxdur")
            Log.d(TAG, "Current userId: $userId")

            val snapshot = realtimeDb.reference.child("artworks")
                .orderByChild("sellerId")
                .equalTo(userId)
                .get()
                .await()

            Log.d(TAG, "Retrieved ${snapshot.childrenCount} artworks")

            val artworks = mutableListOf<Artwork>()
            for (childSnapshot in snapshot.children) {
                val artwork = childSnapshot.getValue<Artwork>()
                artwork?.let {
                    Log.d(TAG, "Artwork: ${it.id}, imageUrl: ${it.imageUrl}")
                    artworks.add(it)
                }
            }

            // Zamana görə sıralama
            artworks.sortByDescending { it.createdAt }

            emit(Resource.Success(artworks))

        } catch (e: Exception) {
            Log.e(TAG, "Error getting seller artworks: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "Əsərləri əldə etmək mümkün olmadı"))
        }
    }

    // Bütün əsərləri əldə etmək (Realtime Database istifadə edərək)
    fun getAllArtworks(): Flow<Resource<List<Artwork>>> = flow {
        emit(Resource.Loading())

        try {
            Log.d(TAG, "Getting all artworks")

            val snapshot = realtimeDb.reference.child("artworks")
                .get()
                .await()

            Log.d(TAG, "Retrieved ${snapshot.childrenCount} artworks")

            val artworks = mutableListOf<Artwork>()
            for (childSnapshot in snapshot.children) {
                val artwork = childSnapshot.getValue<Artwork>()
                artwork?.let {
                    Log.d(TAG, "Artwork: ${it.id}, imageUrl: ${it.imageUrl}")
                    artworks.add(it)
                }
            }

            // Zamana görə sıralama
            artworks.sortByDescending { it.createdAt }

            emit(Resource.Success(artworks))

        } catch (e: Exception) {
            Log.e(TAG, "Error getting all artworks: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "Əsərləri əldə etmək mümkün olmadı"))
        }
    }

    // Əsər silmək (Realtime Database istifadə edərək)
    suspend fun deleteArtwork(artworkId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            Log.d(TAG, "Deleting artwork: $artworkId")

            val userId = auth.currentUser?.uid ?: throw Exception("İstifadəçi girişi yoxdur")
            Log.d(TAG, "Current userId: $userId")

            // Əvvəlcə əsəri yoxlayırıq (təhlükəsizlik üçün)
            val artworkSnapshot = realtimeDb.reference.child("artworks").child(artworkId).get().await()
            val artwork = artworkSnapshot.getValue<Artwork>()

            if (artwork == null) {
                Log.e(TAG, "Artwork not found for deletion: $artworkId")
                throw Exception("Əsər tapılmadı")
            }

            if (artwork.sellerId != userId) {
                Log.e(TAG, "User does not have permission to delete artwork: $artworkId")
                throw Exception("Bu əsəri silmək üçün icazəniz yoxdur")
            }

            // Əsərin şəkli varsa, Storage-dən silirik
            if (artwork.imageUrl.isNotEmpty()) {
                try {
                    Log.d(TAG, "Attempting to delete image from storage: ${artwork.imageUrl}")

                    // Clean the URL by removing query parameters if present
                    val cleanUrl = artwork.imageUrl.split("?")[0]
                    val imageRef = storage.getReferenceFromUrl(cleanUrl)
                    imageRef.delete().await()

                    Log.d(TAG, "Image deleted successfully from storage")
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting image from storage: ${e.message}", e)
                    // Continue with deletion even if image deletion fails
                }
            }

            // Realtime Database-dən silirik
            realtimeDb.reference.child("artworks").child(artworkId).removeValue().await()
            Log.d(TAG, "Artwork deleted successfully from database")

            emit(Resource.Success(true))

        } catch (e: Exception) {
            Log.e(TAG, "Error deleting artwork: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "Əsəri silmək mümkün olmadı"))
        }
    }

    // İstifadəçi qeydiyyatı (Firestore ilə saxlayırıq)
    suspend fun registerUser(email: String, password: String, name: String, isSeller: Boolean): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {
            Log.d(TAG, "Registering new user: $email")

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("İstifadəçi yaradıla bilmədi")
            Log.d(TAG, "User created with ID: $userId")

            val user = hashMapOf(
                "id" to userId,
                "name" to name,
                "email" to email,
                "bio" to "",
                "profilePictureUrl" to "",
                "isSeller" to isSeller,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users").document(userId).set(user).await()
            Log.d(TAG, "User data stored in Firestore")

            emit(Resource.Success(userId))

        } catch (e: Exception) {
            Log.e(TAG, "Error registering user: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "Qeydiyyat zamanı xəta baş verdi"))
        }
    }

    // İstifadəçi girişi
    suspend fun loginUser(email: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {
            Log.d(TAG, "Logging in user: $email")

            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Giriş zamanı xəta baş verdi")
            Log.d(TAG, "User logged in with ID: $userId")

            emit(Resource.Success(userId))

        } catch (e: Exception) {
            Log.e(TAG, "Error logging in: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "Giriş zamanı xəta baş verdi"))
        }
    }

    // Cari istifadəçini əldə etmək
    suspend fun getCurrentUser(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())

        try {
            Log.d(TAG, "Getting current user data")

            val userId = auth.currentUser?.uid ?: throw Exception("İstifadəçi girişi yoxdur")
            Log.d(TAG, "Current userId: $userId")

            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("İstifadəçi məlumatları tapılmadı")
            Log.d(TAG, "User data retrieved successfully")

            emit(Resource.Success(user))

        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user: ${e.message}", e)
            emit(Resource.Error(e.localizedMessage ?: "İstifadəçi məlumatlarını əldə etmək mümkün olmadı"))
        }
    }
}