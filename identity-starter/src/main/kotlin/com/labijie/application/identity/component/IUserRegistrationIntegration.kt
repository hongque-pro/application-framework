/**
 * @author Anders Xiao
 * @date 2024-06-14
 */
package com.labijie.application.identity.component

import com.labijie.application.identity.model.UserAndRoles


interface IUserRegistrationIntegration {
    fun onUserRegisteredInTransaction(user: UserAndRoles, addition: Map<String, String>) {}
    fun onUserRegisteredAfterTransactionCommitted(user: UserAndRoles, addition: Map<String, String>) {}
}