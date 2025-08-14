package com.github.yuxiaoyao.arthasideahelper.runner;


import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author kerryzhang on 2025/08/07
 */

public class ArthasRunMenuActionGroup extends ActionGroup {
    private AnAction[] children;


    public ArthasRunMenuActionGroup() {
        super("Arthas Menu", true);

        setPopup(true);
        this.children = new AnAction[]{
        };


    }


    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent anActionEvent) {
        return this.children;
    }
}
