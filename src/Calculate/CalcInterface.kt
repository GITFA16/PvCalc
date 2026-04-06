package Calculate

import Data.PVOutDC
import Data.PvBase
import Userinterface.UserInputDC

interface CalcInterface {
      fun calculation(
          ui: UserInputDC,
          modul: PvBase
        ): PVOutDC


}