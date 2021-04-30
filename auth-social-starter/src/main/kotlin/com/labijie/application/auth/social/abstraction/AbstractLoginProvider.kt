package com.labijie.application.auth.social.abstraction

import com.labijie.application.auth.social.SocialAuthOptions
import com.labijie.application.identity.social.ILoginProvider

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
abstract class AbstractLoginProvider<TOptions: SocialAuthOptions>(
    protected val options: TOptions
) : ILoginProvider {
    override val name: String
        get() = this.options.providerName
}