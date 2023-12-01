package com.labijie.application.identity.model

import com.labijie.application.identity.data.pojo.Role
import com.labijie.application.identity.data.pojo.User

class SocialUserAndRoles(
    user: User,
    roles: List<Role>,
    val loginProvider: String,
    val loginProviderKey: String
) : UserAndRoles(user, roles) {
    constructor(userAndRoles: UserAndRoles, loginProvider: String, loginProviderKey: String) : this(
        userAndRoles.user,
        userAndRoles.roles,
        loginProvider,
        loginProviderKey
    )
}