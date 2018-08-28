package data.custom;

import data.core.GeneratingContentInfo;
import data.core.JavaBoyceTemplate;
import data.domain.JavaProjectInfo;
import data.domain.Table;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.jtwig.JtwigModel;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

/**
 * @author boyce - 2018/3/31.
 */
@Slf4j
public class MyBatisMVCTemplateBase {

    private static Map<String, String> TEMPLATE_NAME_MAP = new HashMap<>();
    private static Map<String, String> FILE_NAME_MAP = new HashMap<>();
    private static Map<String, String> FILE_PATH_MAP = new HashMap<>();
    private static Map<String, String> FILE_SUFFIX_MAP = new HashMap<>();

    static {

        TEMPLATE_NAME_MAP.put("POJO", "pojo_template.twig");
        FILE_NAME_MAP.put("pojo_template.twig", "");
        FILE_PATH_MAP.put("pojo_template.twig", "/domain/");
        FILE_SUFFIX_MAP.put("pojo_template.twig", ".java");

        TEMPLATE_NAME_MAP.put("Repository", "mybatis_repository.twig");
        FILE_NAME_MAP.put("mybatis_repository.twig", "Repository");
        FILE_PATH_MAP.put("mybatis_repository.twig", "/data/");
        FILE_SUFFIX_MAP.put("mybatis_repository.twig", ".java");

        TEMPLATE_NAME_MAP.put("MyBatisXml", "mybatis_mapper.twig");
        FILE_NAME_MAP.put("mybatis_mapper.twig", "Mapper");
        FILE_PATH_MAP.put("mybatis_mapper.twig", "/data/");
        FILE_SUFFIX_MAP.put("mybatis_mapper.twig", ".xml");

        TEMPLATE_NAME_MAP.put("Example", "mybatis_example.twig");
        FILE_NAME_MAP.put("mybatis_example.twig", "Example");
        FILE_PATH_MAP.put("mybatis_example.twig", "/domain/example/");
        FILE_SUFFIX_MAP.put("mybatis_example.twig", ".java");

        TEMPLATE_NAME_MAP.put("Service", "mybatis_service.twig");
        FILE_NAME_MAP.put("mybatis_service.twig", "Service");
        FILE_PATH_MAP.put("mybatis_service.twig", "/service/");
        FILE_SUFFIX_MAP.put("mybatis_service.twig", ".java");

        TEMPLATE_NAME_MAP.put("ServiceImpl", "mybatis_service_impl.twig");
        FILE_NAME_MAP.put("mybatis_service_impl.twig", "ServiceImpl");
        FILE_PATH_MAP.put("mybatis_service_impl.twig", "/service/impl/");
        FILE_SUFFIX_MAP.put("mybatis_service_impl.twig", ".java");
    }

    private final String templateLocation;
    private String outputDir = "target/";
    private Set<JavaBoyceTemplate> javaTemplateSet;
    private List<Table> tableList;
    private JavaProjectInfo javaProjectInfo;

    private JtwigModel basicModel;

    public MyBatisMVCTemplateBase(JavaProjectInfo javaProjectInfo, String templateLocation) {
        this.templateLocation = templateLocation;
        this.javaProjectInfo = javaProjectInfo;
    }

    /**
     * 生成文件
     *
     * @param generatingContentInfoList 需要的数据
     * @param selectedTemplateNameList  要生成的模板
     * @throws SQLException sqlException
     */
    public Task generate(List<GeneratingContentInfo> generatingContentInfoList, List<String> selectedTemplateNameList) {
        return new Task() {

            @Override
            protected Object call() {
                try {
                    double max = 100D;
                    int totalStep = 3;
                    int step = 0;
                    double preProgress = max / totalStep;
                    double currentProgress = 0D;
                    updateMessage("building skeleton");
                    buildSkeleton(MyBatisMVCTemplateBase.this.outputDir +
                        MyBatisMVCTemplateBase.this.javaProjectInfo.getPackagePath(), selectedTemplateNameList);
                    updateMessage("built skeleton");
                    currentProgress = ++step * preProgress;
                    updateProgress(currentProgress, max);

                    updateMessage("Initialing template models");
                    initBasicModel();
                    updateMessage("Initialed template models");
                    currentProgress = ++step * preProgress;
                    updateProgress(currentProgress, max);

                    updateMessage("Rendering template");

                    int subTotalStep = generatingContentInfoList.size() * 3;
                    int subStep = 0;
                    double perSubProgress = preProgress / subTotalStep;
                    double subCurProgress = 0D;
                    for (GeneratingContentInfo generatingContentInfo : generatingContentInfoList) {

                        updateMessage("Generating " + generatingContentInfo.getDbName());
                        updateMessage("Loading tableInfo");
                        loadTableList(generatingContentInfo);
                        updateMessage("loaded tableInfo");
                        subCurProgress = ++subStep * perSubProgress;
                        updateProgress(currentProgress + subCurProgress, max);

                        updateMessage("Preparing template");
                        prepareTemplate(selectedTemplateNameList, generatingContentInfo);
                        updateMessage("Prepared template");
                        subCurProgress = ++subStep * perSubProgress;
                        updateProgress(currentProgress + subCurProgress, max);

                        int subSubTotalStep = javaTemplateSet.size();
                        int subSubStep = 0;
                        double perSubSubProgress = perSubProgress / subSubTotalStep;
                        double subSubCurProgress;
                        for (JavaBoyceTemplate javaBoyceTemplate : MyBatisMVCTemplateBase.this.javaTemplateSet) {
                            updateMessage("Rendering " + javaBoyceTemplate.getName());
                            javaBoyceTemplate.render();
                            updateMessage("Rendered " + javaBoyceTemplate.getName());
                            subSubCurProgress = ++subSubStep * perSubSubProgress;
                            updateProgress(currentProgress + subCurProgress + subSubCurProgress, max);
                        }
                        subStep++;
                    }
                    updateMessage("Finished");
                    return true;
                } catch (Exception e) {
                    log.error("", e);
                    return false;
                }
            }
        };

    }

    private void initBasicModel() {
        this.basicModel = JtwigModel.newModel()
            .with("date", new Date())
            .with("author", this.javaProjectInfo.getAuthor())
            .with("groupId", this.javaProjectInfo.getGroupId());
    }

    private void loadTableList(GeneratingContentInfo generatingContentInfo) throws SQLException {
        this.tableList = generatingContentInfo.getBoyceDataSource()
            .loadTable(generatingContentInfo.getDbName(), generatingContentInfo.getTableNameList());
        this.tableList.forEach(table -> table.setTablePrefix(this.javaProjectInfo.getTablePrefix()));
    }

    private void prepareTemplate(List<String> selectedTemplateNameList, GeneratingContentInfo generatingContentInfo) {
        this.javaTemplateSet = new LinkedHashSet<>();
        for (Map.Entry<String, String> stringStringEntry : TEMPLATE_NAME_MAP.entrySet()) {
            String templateFile = stringStringEntry.getValue();
            String templateName = FILE_NAME_MAP.get(templateFile);
            if (selectedTemplateNameList.contains(stringStringEntry.getKey())) {
                for (Table table : this.tableList) {
                    JavaBoyceTemplate javaBoyceTemplate = JavaBoyceTemplate.builder()
                        .dataSource(generatingContentInfo.getBoyceDataSource())
                        .dbName(generatingContentInfo.getDbName())
                        .table(table)
                        .location(this.templateLocation + templateFile)
                        .name(templateName)
                        .suffix(FILE_SUFFIX_MAP.get(templateFile))
                        .packagePath(this.javaProjectInfo.getPackagePath() + FILE_PATH_MAP.get
                            (templateFile))
                        .model(this.basicModel)
                        .outputDir(this.outputDir)
                        .build();
                    this.javaTemplateSet.add(javaBoyceTemplate);
                }
            }
        }
    }

    /**
     * skeleton
     * package/domain/*
     * |      |----/example/*Example
     * |----/data/*Repository
     * |----/service/*Service
     * |----/impl/*ServiceImpl
     */
    private void buildSkeleton(String packagePath, List<String> generatingTableNameList) {
        File domainDir = new File(packagePath + "/domain/");
        File exampleDir = new File(packagePath + "/domain/example/");
        File repositoryDir = new File(packagePath + "/data/");
        File serviceDir = new File(packagePath + "/service/");
        File serviceImplDir = new File(packagePath + "/service/impl/");
        if (generatingTableNameList.contains("POJO")) {
            if (!domainDir.exists()) {
                if (!domainDir.mkdirs()) {
                    log.error("Failed make directory {} ", domainDir.getAbsolutePath());
                }
            }
        }
        if (generatingTableNameList.contains("Example")) {
            if (!exampleDir.exists()) {
                if (!exampleDir.mkdirs()) {
                    log.error("Failed make directory {} ", exampleDir.getAbsolutePath());
                }
            }
        }
        if (generatingTableNameList.contains("Repository") || generatingTableNameList.contains("MyBatisXml")) {
            if (!repositoryDir.exists()) {
                if (!repositoryDir.mkdirs()) {
                    log.error("Failed make directory {} ", repositoryDir.getAbsolutePath());
                }
            }
        }
        if (generatingTableNameList.contains("Service")) {
            if (!serviceDir.exists()) {
                if (!serviceDir.mkdirs()) {
                    log.error("Failed make directory {} ", serviceDir.getAbsolutePath());
                }
            }
        }
        if (generatingTableNameList.contains("ServiceImpl")) {
            if (!serviceImplDir.exists()) {
                if (!serviceImplDir.mkdirs()) {
                    log.error("Failed make directory {} ", serviceImplDir.getAbsolutePath());
                }
            }
        }
    }

    public List<Table> getTableList() {
        return this.tableList;
    }

}
