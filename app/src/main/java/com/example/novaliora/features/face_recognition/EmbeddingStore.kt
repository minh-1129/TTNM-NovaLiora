package com.example.novaliora.features.face_recognition


import android.content.Context
import com.example.novaliora.R
import com.example.novaliora.features.FaceDetectorProvider.faceDetector
import com.google.mlkit.vision.common.InputImage
import org.tensorflow.lite.support.image.TensorImage


object EmbeddingStore {
    private var storedImageEmbeddings: List<FloatArray> = mutableListOf()

    fun initialize(context: Context, faceNetModel: FaceNetModel) {
        if (storedImageEmbeddings.isEmpty()) {
            storedImageEmbeddings = embeddingStoredImages(context, faceNetModel)
        }
    }

    fun getEmbeddings(): List<FloatArray> {
        return storedImageEmbeddings
    }

    private fun embeddingStoredImages(context: Context, faceNetModel: FaceNetModel): List<FloatArray> {
        val famousImages = listOf(
            R.drawable.david_beckham,
            R.drawable.phuoc1,
            R.drawable.phuoc2,
            R.drawable.phuoc3
        )

        val embeddings = mutableListOf<FloatArray>()

        famousImages.forEach { drawableResId ->
            val bitmap = loadDrawableAsBitmap(context, drawableResId)
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    faces.forEach { face ->
                        val faceBitmap = cropFace(bitmap, face.boundingBox, 0f)
                        if (faceBitmap != null) {
                            val tensorImage = TensorImage.fromBitmap(faceBitmap)
                            val preprocessedImage = preprocessImage(tensorImage)
                            val embedding = faceNetModel.getEmbedding(preprocessedImage)
                            embeddings.add(embedding)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
        return embeddings
    }
}