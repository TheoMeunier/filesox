package fr.tmeunier.domaine.services.filesSystem.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.createMultipartUpload
import aws.sdk.kotlin.services.s3.model.*
import aws.smithy.kotlin.runtime.content.ByteStream
import fr.tmeunier.config.S3Config

object S3UploadService {

    val uploads = mutableMapOf<String, MutableList<CompletedPart>>()

    suspend fun initiateMultipartUpload(client: S3Client, key: String): String? {
        val multipartRes = client.createMultipartUpload {
            checksumAlgorithm = ChecksumAlgorithm.Sha256
            bucket = S3Config.bucketName
            this.key = key
        }

        uploads[multipartRes.uploadId!!] = mutableListOf()

        return multipartRes.uploadId
    }

    suspend fun uploadMultipart(
        client: S3Client, key: String, uploadId: String?, chunkNumber: Int, fileBytes: ByteArray?, totalChunks: Int
    ): String? {
        try {
            val part = client.uploadPart(UploadPartRequest {
                bucket = S3Config.bucketName
                this.key = key
                this.uploadId = uploadId
                partNumber = chunkNumber
                body = ByteStream.fromBytes(fileBytes!!)
            }).let {
                CompletedPart {
                    checksumSha256 = it.checksumSha256
                    partNumber = chunkNumber
                    eTag = it.eTag
                }
            }

            uploads[uploadId!!]?.add(part)

        } catch (e: S3Exception) {
            println("Error uploading file: ${e.message}")
        }

        return uploadId
    }

    suspend fun completeMultipartUpload(client: S3Client, remotePath: String, uplId: String?) {
        client.completeMultipartUpload(CompleteMultipartUploadRequest {
            bucket = S3Config.bucketName
            this.key = remotePath
            this.uploadId = uplId
            multipartUpload = CompletedMultipartUpload {
                parts = uploads[uplId!!]
                bucket = S3Config.bucketName
                key = remotePath
                uploadId = uplId
            }
        }).also { uploads.remove(uplId) }
    }
}