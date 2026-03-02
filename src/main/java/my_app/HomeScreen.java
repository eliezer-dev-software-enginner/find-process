package my_app;

import megalodonte.ComputedState;
import megalodonte.Show;
import megalodonte.State;
import megalodonte.base.Redirect;
import megalodonte.components.*;
import megalodonte.components.inputs.Input;
import megalodonte.props.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeScreen {
    State<String> port = State.of("");
    State<String> processosPIDS = State.of("");

    Set<String> pidsCache = new HashSet<>();
    State<Boolean> descricaoResultIsVisible = State.of(false);
    State<String> descricaoResult = State.of("");

    public Component render() {
        return new Column(new ColumnProps().paddingAll(20).bgColor("#fff"))
                .children(
                        new Button("Siga-me no Github").onClick(()-> Redirect.to("https://github.com/eliezer-dev-software-enginner")),
                        new SpacerVertical(10),
                        new Text("Descubra processos que estão utilizando a porta"),
                        new Input(port, new InputProps().placeHolder("Ex: 3000").borderColor("black")),
                        new SpacerVertical(10),
                        new Button("Listar").onClick(this::handleClickListar),
                        new Text(processosPIDS),
                        new Button("Encerrar processos", new ButtonProps().bgColor("red")).onClick(this::handleClickEncerrarProcessos),
                        new SpacerVertical(20),
                        Show.when(descricaoResultIsVisible ,()->  new Text(descricaoResult)),
                        new SpacerVertical(20),
                        new Text("Criado por Eliezer - 2026", new TextProps().fontSize(12))
                );
    }

    void handleClickListar(){
        descricaoResultIsVisible.set(false);
        descricaoResult.set("");

        processosPIDS.set("");
        var pids = getPIDS();

        if(pids.isEmpty()){
            descricaoResultIsVisible.set(true);
            descricaoResult.set("Processos não encontrados!");
        }

        pidsCache.addAll(pids);

        for (String pid : pids) {
            processosPIDS.set(processosPIDS.get() + " | " + pid + " |");
        }
    }

    void handleClickEncerrarProcessos(){
        descricaoResultIsVisible.set(false);
        descricaoResult.set("");
        for (String pid : pidsCache) {
           var pb = new ProcessBuilder("cmd","/c", String.format("taskkill /PID %s /F", pid));
            try {
                Process process = pb.start();
                process.waitFor();
                descricaoResultIsVisible.set(true);
                descricaoResult.set("Processos encerrados");
            } catch (Exception e) {
                descricaoResultIsVisible.set(true);
                descricaoResult.set("Erro: " + e.getMessage());
            }
        }
    }

    Set<String> getPIDS(){
        var list = new HashSet<String>();

            try{
                ProcessBuilder pb = new ProcessBuilder("netstat", "-ano");
                Process process = pb.start();

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(":3000")  && line.contains("LISTENING")) {
                        System.out.println(line);
                        // remove espaços extras e divide
                        String[] parts = line.trim().split("\\s+");

                        String pid = parts[parts.length - 1];
                        System.out.println("PID encontrado: " + pid);
                        list.add(pid);
                    }
                }
                process.waitFor();
                return list;
            }catch (Exception e){
                descricaoResultIsVisible.set(true);
                descricaoResult.set("Erro: " + e.getMessage());
                return null;
            }
    }
}
