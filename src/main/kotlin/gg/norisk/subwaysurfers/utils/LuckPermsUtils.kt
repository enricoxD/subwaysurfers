package gg.norisk.subwaysurfers.utils

import gg.norisk.subwaysurfers.utils.LuckPermsUtils.permissionData
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.minecraft.server.network.ServerPlayerEntity

object LuckPermsUtils {
    val LuckPerms by lazy { LuckPermsProvider.get() }

    val ServerPlayerEntity.lpUser: User
        get() = LuckPerms.getPlayerAdapter(ServerPlayerEntity::class.java).getUser(this)

    val ServerPlayerEntity.permissionData
        get() = LuckPerms.getPlayerAdapter(ServerPlayerEntity::class.java).getPermissionData(this)
}

fun ServerPlayerEntity.hasPermission(permission: String): Boolean {
    return permissionData.checkPermission(permission).asBoolean()
}
