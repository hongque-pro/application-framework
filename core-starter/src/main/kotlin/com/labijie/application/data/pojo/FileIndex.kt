@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`data`.pojo

import com.labijie.application.model.FileModifier
import kotlin.Long
import kotlin.String

/**
 * POJO for FileIndexTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.data.FileIndexTable
 */
public open class FileIndex {
  public var path: String = ""

  public var timeCreated: Long = 0L

  public var fileType: String = ""

  public var entityId: Long = 0L

  public var fileAccess: FileModifier = FileModifier.Public

  public var id: Long = 0L
}
