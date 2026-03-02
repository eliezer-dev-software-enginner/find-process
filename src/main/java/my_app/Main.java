package my_app;

import megalodonte.ListenerManager;
import megalodonte.application.Context;
import megalodonte.application.MegalodonteApp;
import megalodonte.components.*;
import my_app.hotreload.HotReload;

import java.util.Set;

public class Main {
    static HotReload hotReload;
    static boolean devMode = true;

    static void main() {
        MegalodonteApp.run(context -> {
            final var stage = context.javafxStage();
            stage.setTitle("find-process by Eliezer");
            stage.setWidth(500);
            stage.setHeight(300);
            stage.setResizable(false);

            initialize(context);

            MegalodonteApp.onShutdown(() -> {
                System.out.println("Clicked on X - close application");
                hotReload.stop();
                ListenerManager.disposeAll();
            });
        });
    }

    public static void initialize(Context context) {
        context.useView(new HomeScreen().render());

        if (devMode) {
           hotReload = new HotReload()
                .sourcePath("src/main/java")
                .classesPath("build/classes/java/main")
                .resourcesPath("src/main/resources")
                .implementationClassName("my_app.hotreload.UIReloaderImpl")
                .screenClassName("my_app.HomeScreen")
                .reloadContext(context)
                .classesToExclude(Set.of(
                    "my_app.Main",
                    "my_app.hotreload.Reloader",
                    "my_app.hotreload.UIReloaderImpl",
                    "my_app.hotreload.HotReload",
                    "my_app.hotreload.HotReloadClassLoader"
                ));
           hotReload.start();
        }
    }
}