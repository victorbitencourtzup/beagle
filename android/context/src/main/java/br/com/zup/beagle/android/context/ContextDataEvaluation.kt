/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle.android.context

import br.com.zup.beagle.android.logger.BeagleContextLogs
import br.com.zup.beagle.android.jsonpath.JsonPathFinder
import br.com.zup.beagle.android.utils.getContextId
import br.com.zup.beagle.android.utils.getExpressions
import com.squareup.moshi.Moshi
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type

//TODO REMOVED INTERNAL
class ContextDataEvaluation(
    private val jsonPathFinder: JsonPathFinder = JsonPathFinder(),
    private val contextPathResolver: ContextPathResolver = ContextPathResolver(),
    private val moshi: Moshi = ContextConstant.moshi
) {

    fun evaluateBindExpression(
        contextData: ContextData,
        bind: Bind.Expression<*>
    ): Any? {
        return evaluateBindExpression(listOf(contextData), bind)
    }

    fun evaluateBindExpression(
        contextsData: List<ContextData>,
        bind: Bind.Expression<*>
    ): Any? {
        val expressions = bind.value.getExpressions()

        return when {
            bind.type == String::class.java -> {
                contextsData.forEach { contextData ->
                    expressions.filter { it.getContextId() == contextData.id }.forEach { expression ->
                        evaluateExpressionsForContext(contextData, expression, bind)
                    }
                }

                evaluateMultipleExpressions(bind)
            }
            expressions.size == 1 -> evaluateExpression(contextsData[0], bind, expressions[0])
            else -> {
                BeagleContextLogs.multipleExpressionsInValueThatIsNotString()
                null
            }
        }
    }

    private fun evaluateExpressionsForContext(
        contextData: ContextData,
        expression: String,
        bind: Bind.Expression<*>
    ) {
        val value = evaluateExpression(contextData, bind, expression) ?: ""
        bind.evaluatedExpressions[expression] = value
    }

    private fun evaluateMultipleExpressions(bind: Bind.Expression<*>): Any? {
        var text = bind.value
        bind.evaluatedExpressions.forEach {
            val expressionKey = it.key
            text = text.replace("@{$expressionKey}", it.value.toString())
        }
        return if (text.isEmpty()) null else text
    }

    private fun evaluateExpression(contextData: ContextData, bind: Bind.Expression<*>, expression: String): Any? {
        val value = getValue(contextData, expression, bind.type)

        return try {
            if (bind.type == String::class.java) {
                value?.toString() ?: showLogErrorAndReturn(bind)
            } else if (value is JSONArray || value is JSONObject) {
                moshi.adapter<Any>(bind.type).fromJson(value.toString()) ?: showLogErrorAndReturn(bind)
            } else {
                value ?: showLogErrorAndReturn(bind)
            }
        } catch (ex: Exception) {
            BeagleContextLogs.errorWhileTryingToNotifyContextChanges(ex)
            null
        }
    }

    private fun showLogErrorAndReturn(bind: Bind.Expression<*>) = run {
        BeagleContextLogs.errorWhenExpressionEvaluateNullValue("${bind.value} : ${bind.type}")
        null
    }

    private fun getValue(contextData: ContextData, path: String, type: Type): Any? {
        return if (path != contextData.id) {
            findValue(contextData, path)
        } else {
            ContextValueHandler.treatValue(contextData.value, type)
        }
    }

    private fun findValue(contextData: ContextData, path: String): Any? {
        return try {
            val keys = contextPathResolver.getKeysFromPath(contextData.id, path)
            jsonPathFinder.find(keys, contextData.value)
        } catch (ex: Exception) {
            BeagleContextLogs.errorWhileTryingToAccessContext(ex)
            null
        }
    }
}