package Calculate

import Data.PVOutDC
import Userinterface.UserInputDC

interface CalcInterface {
      fun calculation(
          ui: UserInputDC,
          modul: String
        ): PVOutDC


}