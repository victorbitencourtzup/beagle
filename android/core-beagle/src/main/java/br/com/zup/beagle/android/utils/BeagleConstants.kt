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

package br.com.zup.beagle.android.utils

import br.com.zup.beagle.android.manager.StyleManager
import br.com.zup.beagle.android.view.ViewFactory

object BeagleConstants {
    const val DEPRECATED_PAGE_VIEW =
        "This constructor will be removed in a future version, use the constructor with Bind"

    const val FORM_DEPRECATED_MESSAGE = "use SimpleForm and SubmitForm instead"
}

object HandleEventDeprecatedConstants {
    const val HANDLE_EVENT_DEPRECATED_MESSAGE: String =
        "Use handleEvent without eventName and eventValue or with ContextData for create a implicit context"
    const val HANDLE_EVENT_POINTER: String = "handleEvent(rootView, origin, action)"
    const val HANDLE_EVENT_ACTIONS_POINTER: String = "handleEvent(rootView, origin, actions)"
}

//TODO REMOVED INTERNAL
internal val viewExtensionsViewFactory = ViewFactory()
internal val styleManagerFactory = StyleManager()