package Data.API

import Data.ApiOutputDC
import Userinterface.UserInputDC

interface ApiInterface {
    fun apiCollect(ui: UserInputDC): ApiOutputDC
}