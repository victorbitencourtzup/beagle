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

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.zup.beagle.android.components.utils.viewExtensionsViewFactory
import br.com.zup.beagle.android.data.serializer.BeagleSerializer
import br.com.zup.beagle.android.engine.renderer.ActivityRootView
import br.com.zup.beagle.android.engine.renderer.FragmentRootView
import br.com.zup.beagle.android.view.BeagleFragment
import br.com.zup.beagle.android.view.ScreenRequest
import br.com.zup.beagle.android.view.custom.OnStateChanged
import br.com.zup.beagle.android.view.viewmodel.ScreenContextViewModel
import br.com.zup.beagle.android.widget.RootView

internal var beagleSerializerFactory = BeagleSerializer()

/**
 * Load a ServerDrivenComponent into this ViewGroup
 * @property activity that is parent of this view
 * @property screenRequest to create your request data to fetch the component
 * @property listener is called when the loading is started and finished
 */
fun ViewGroup.loadView(activity: AppCompatActivity, screenRequest: ScreenRequest, listener: OnStateChanged? = null) {
    loadView(this, ActivityRootView(activity), screenRequest, listener)
}

/**
 * Load a ServerDrivenComponent into this ViewGroup
 * @property fragment that is parent of this view
 * @property screenRequest to create your request data to fetch the component
 * @property listener is called when the loading is started and finished
 */
fun ViewGroup.loadView(fragment: Fragment, screenRequest: ScreenRequest, listener: OnStateChanged? = null) {
    loadView(this, FragmentRootView(fragment), screenRequest, listener)
}

private fun loadView(
    viewGroup: ViewGroup,
    rootView: RootView,
    screenRequest: ScreenRequest,
    listener: OnStateChanged?
) {
    val view = viewExtensionsViewFactory.makeBeagleView(viewGroup.context).apply {
        loadView(rootView, screenRequest)
        stateChangedListener = listener
    }
    view.loadCompletedListener = {
        viewGroup.addView(view)
        rootView.generateViewModelInstance<ScreenContextViewModel>().evaluateContexts()
    }
}

/**
 * Render a ServerDrivenComponent into this ViewGroup
 * @property activity that is parent of this view.
 * Make sure to use this method if you are inside a Activity because of the lifecycle
 * @property screenJson that represents your component
 */
fun ViewGroup.renderScreen(activity: AppCompatActivity, screenJson: String) {
    this.renderScreen(ActivityRootView(activity), screenJson)
}

/**
 * Render a ServerDrivenComponent into this ViewGroup
 * @property fragment <p>that is parent of this view.
 * Make sure to use this method if you are inside a Fragment because of the lifecycle</p>
 * @property screenJson that represents your component
 */
fun ViewGroup.renderScreen(fragment: Fragment, screenJson: String) {
    this.renderScreen(FragmentRootView(fragment), screenJson)
}

internal fun ViewGroup.renderScreen(rootView: RootView, screenJson: String) {
    val viewModel = rootView.generateViewModelInstance<ScreenContextViewModel>()
    viewModel.clearContexts()
    val component = beagleSerializerFactory.deserializeComponent(screenJson)
    (rootView.getContext() as AppCompatActivity)
        .supportFragmentManager
        .beginTransaction()
        .replace(this.id, BeagleFragment.newInstance(component))
        .addToBackStack(null)
        .commit()
}
