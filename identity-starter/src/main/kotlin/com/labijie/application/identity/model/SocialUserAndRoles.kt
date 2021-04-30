package com.labijie.application.identity.model

import com.labijie.application.identity.data.RoleRecord
import com.labijie.application.identity.data.UserRecord

class SocialUserAndRoles(
    user: UserRecord,
    roles: List<RoleRecord>,
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