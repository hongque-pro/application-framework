package com.labijie.application.auth.social.model

import com.labijie.application.auth.data.RoleRecord
import com.labijie.application.auth.data.UserRecord
import com.labijie.application.auth.model.UserAndRoles

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