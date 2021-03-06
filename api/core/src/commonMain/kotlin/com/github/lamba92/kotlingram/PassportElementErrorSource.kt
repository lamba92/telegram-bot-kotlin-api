package com.github.lamba92.kotlingram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PassportElementErrorSource {
    @SerialName("data") DATA,
    @SerialName("front_side") FRONT_SIDE,
    @SerialName("reverse_side") REVERSE_SIDE,
    @SerialName("selfie") SELFIE,
    @SerialName("file") FILE,
    @SerialName("files") FILES,
    @SerialName("translation_file") TRANSLATION_FILE,
    @SerialName("translation_files") TRANSLATION_FILES,
    @SerialName("unspecified") UNSPECIFIED
}
