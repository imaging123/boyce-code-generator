package data.core;

import data.domain.Table;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.join;

/**
 * @author boyce - 01/14/2018
 */
@Slf4j
@NoArgsConstructor
public abstract class BoyceTemplate {
    private JtwigTemplate template;
    private JtwigModel model;
    private Table table;

    /**
     * Render resources.template with model's attributes
     *
     * @return model
     */
    protected abstract JtwigModel prepareModel();

    /**
     * A table have name
     *
     * @return table
     */
    protected abstract Table prepareTable();

    /**
     * Load resources.template file
     *
     * @return resources.template
     */
    protected abstract JtwigTemplate loadTemplate();

    /**
     * Fill table's attributes
     *
     * @param nowTable Currency table bean
     * @return Table with all attributes
     */
    protected abstract Table fillTable(Table nowTable);

    /**
     * 获取FileName
     *
     * @param table table
     * @return fileName
     */
    protected abstract String getFileName(Table table);

    public void render() {
        init();

        File f = new File(getFileName(table));
        if (!f.exists()) {
            try {
                if (!f.createNewFile()) {
                    log.error("Failed create file {}", f.getAbsoluteFile());
                }
            } catch (IOException ignored) {
            }
        }
        try {
            this.template.render(this.model, new FileOutputStream(f));
        } catch (FileNotFoundException e) {
            log.error("", e);
        }
    }

    private void init() {
        model = prepareModel();
        template = loadTemplate();
        table = prepareTable();
        table = fillTable(table);
        if (model == null) {
            throw new NullPointerException("Model is null");
        }
        Set<String> imports = table.getColumnList()
            .stream()
            .filter(c -> c.getImports() != null && !c.getImports().startsWith("java.lang"))
            .map(importStr -> "import " + importStr.getImports() + ";")
            .collect(Collectors.toSet());
        this.model
            .with("table", table)
            .with("imports", join(imports, "\n"));
        if (template == null) {
            throw new NullPointerException("Template is null");
        }
    }

}
