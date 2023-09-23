package cc.mewcraft.worldreset.listener

import cc.mewcraft.mewcore.listener.AutoCloseableListener
import cc.mewcraft.worldreset.manager.ServerLock

class WorldListener(
    serverLocks: ServerLock,
) : AutoCloseableListener