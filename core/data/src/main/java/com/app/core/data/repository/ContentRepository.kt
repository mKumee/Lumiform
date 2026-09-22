package com.app.core.data.repository

import com.app.core.common.result.DataResult
import com.app.core.model.Page
import kotlinx.coroutines.flow.Flow

interface ContentRepository {

    fun observeContent(): Flow<DataResult<List<Page>>>
}
