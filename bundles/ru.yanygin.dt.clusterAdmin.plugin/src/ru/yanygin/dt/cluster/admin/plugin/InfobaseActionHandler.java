/**
 * Copyright (C) 2021, Yanygin Sergey.
 */
package ru.yanygin.dt.cluster.admin.plugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class InfobaseActionHandler
    extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {

        String commandId = event.getCommand().getId();

        switch (commandId)
        {
        case "ru.yanygin.dt.clusterAdmin.plugin.killAllSession":

            break;

        default:
            break;
        }

//        Map param = event.getParameters();

        return null;
    }

}
