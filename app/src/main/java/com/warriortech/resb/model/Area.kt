
package com.warriortech.resb.model

data class Area(
    val id: Long,
    val name: String,
    val description: String
)
package com.warriortech.resb.model

data class Area(
    val id: Int = 0,
    val name: String,
    val description: String = ""
)
