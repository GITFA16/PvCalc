package Data

import Data.API.ApiOutputDC

data class PVOutDC(
    val apiOut: ApiOutputDC,
    val pvOut: PvOutputsDC
)