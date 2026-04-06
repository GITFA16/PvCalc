package Data.API

import Data.API.ApiOutputDC
import Userinterface.UserInputDC

interface ApiInterface {
    fun apiCollect(ui: UserInputDC): ApiOutputDC
}