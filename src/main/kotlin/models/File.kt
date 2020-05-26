package models

import javax.persistence.*

@Entity
@Table(name = "file")
data class File(

        @Id
        @GeneratedValue
        var id: Int = -1,

        var fileName: String,
        var mimeType: String,

        @Lob
        var byte: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (id != other.id) return false
        if (fileName != other.fileName) return false
        if (mimeType != other.mimeType) return false
        if (!byte.contentEquals(other.byte)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + fileName.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + byte.contentHashCode()
        return result
    }
}
