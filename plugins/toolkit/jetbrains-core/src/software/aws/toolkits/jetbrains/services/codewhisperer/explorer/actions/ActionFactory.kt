// Copyright 2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.codewhisperer.explorer.actions

import com.intellij.openapi.project.Project
import software.aws.toolkits.jetbrains.services.codewhisperer.credentials.CodeWhispererLoginType
import software.aws.toolkits.jetbrains.services.codewhisperer.customization.CodeWhispererModelConfigurator
import software.aws.toolkits.jetbrains.services.codewhisperer.explorer.CodeWhispererExplorerActionManager

interface ActionProvider<T> {
    val pause: T
    val resume: T
    val openCodeReference: T
    val customize: T
    val learn: T
}

fun<T> buildActionList(project: Project, actionProvider: ActionProvider<T>): List<T> {
    val manager = CodeWhispererExplorerActionManager.getInstance()
    val activeConnectionType = manager.checkActiveCodeWhispererConnectionType(project)

    return buildList {
        if (manager.isAutoEnabled()) {
            add(actionProvider.pause)
        } else {
            add(actionProvider.resume)
        }

        add(actionProvider.openCodeReference)

        // We only show this customization node to SSO users who are in CodeWhisperer Gated Preview list
        if (activeConnectionType == CodeWhispererLoginType.SSO &&
            CodeWhispererModelConfigurator.getInstance().shouldDisplayCustomNode(project)
        ) {
            add(actionProvider.customize)
        }

        add(actionProvider.learn)
    }
}
