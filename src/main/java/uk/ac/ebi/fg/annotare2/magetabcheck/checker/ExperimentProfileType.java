package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

public enum ExperimentProfileType {
    ONE_COLOR_MICROARRAY("One-color microarray"),
    TWO_COLOR_MICROARRAY("Two-color microarray"),
    METHYLATION_MICROARRAY("Methylation microarray"),
    SEQUENCING("High-throughput sequencing"),
    SINGLE_CELL_SEQUENCING("Single-cell sequencing"),
    PLANT_ONE_COLOR_MICROARRAY("Plant - One-color microarray"),
    PLANT_TWO_COLOR_MICROARRAY("Plant - Two-color microarray"),
    PLANT_METHYLATION_MICROARRAY("Plant - Methylation microarray"),
    PLANT_SEQUENCING("Plant - High-throughput sequencing"),
    SINGLE_CELL_PLANT_SEQUENCING("Plant - Single-cell sequencing"),
    HUMAN_ONE_COLOR_MICROARRAY("Human - One-color microarray"),
    HUMAN_TWO_COLOR_MICROARRAY("Human - Two-color microarray"),
    HUMAN_METHYLATION_MICROARRAY("Human - Methylation microarray"),
    HUMAN_SEQUENCING("Human - High-throughput sequencing"),
    SINGLE_CELL_HUMAN_SEQUENCING("Human - Single-cell sequencing"),
    ANIMAL_ONE_COLOR_MICROARRAY("Animal - One-color microarray"),
    ANIMAL_TWO_COLOR_MICROARRAY("Animal - Two-color microarray"),
    ANIMAL_METHYLATION_MICROARRAY("Animal - Methylation microarray"),
    ANIMAL_SEQUENCING("Animal - High-throughput sequencing"),
    SINGLE_CELL_ANIMAL_SEQUENCING("Animal - Single-cell sequencing"),
    CELL_LINE_ONE_COLOR_MICROARRAY("Cell line - One-color microarray"),
    CELL_LINE_TWO_COLOR_MICROARRAY("Cell line - Two-color microarray"),
    CELL_LINE_METHYLATION_MICROARRAY("Cell line - Methylation microarray"),
    CELL_LINE_SEQUENCING("Cell line - High-throughput sequencing"),
    SINGLE_CELL_CELL_LINE_SEQUENCING("Cell line - Single-cell sequencing");

    private final String title;

    ExperimentProfileType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isMicroarray(String title) {
        return ONE_COLOR_MICROARRAY.title.equalsIgnoreCase(title) ||
                TWO_COLOR_MICROARRAY.title.equalsIgnoreCase(title) ||
                PLANT_ONE_COLOR_MICROARRAY.title.equalsIgnoreCase(title) ||
                PLANT_TWO_COLOR_MICROARRAY.title.equalsIgnoreCase(title) ||
                HUMAN_ONE_COLOR_MICROARRAY.title.equalsIgnoreCase(title) ||
                HUMAN_TWO_COLOR_MICROARRAY.title.equalsIgnoreCase(title) ||
                ANIMAL_ONE_COLOR_MICROARRAY.title.equalsIgnoreCase(title) ||
                ANIMAL_TWO_COLOR_MICROARRAY.title.equalsIgnoreCase(title) ||
                CELL_LINE_ONE_COLOR_MICROARRAY.title.equalsIgnoreCase(title) ||
                CELL_LINE_TWO_COLOR_MICROARRAY.title.equalsIgnoreCase(title);
    }

    public boolean isSequencing(String title) {
        return SEQUENCING.title.equalsIgnoreCase(title) ||
                PLANT_SEQUENCING.title.equalsIgnoreCase(title) ||
                HUMAN_SEQUENCING.title.equalsIgnoreCase(title) ||
                ANIMAL_SEQUENCING.title.equalsIgnoreCase(title) ||
                CELL_LINE_SEQUENCING.title.equalsIgnoreCase(title);
    }

    public boolean isSingleCell(String title) {
        return SINGLE_CELL_SEQUENCING.title.equalsIgnoreCase(title) ||
                SINGLE_CELL_PLANT_SEQUENCING.title.equalsIgnoreCase(title) ||
                SINGLE_CELL_HUMAN_SEQUENCING.title.equalsIgnoreCase(title) ||
                SINGLE_CELL_ANIMAL_SEQUENCING.title.equalsIgnoreCase(title) ||
                SINGLE_CELL_CELL_LINE_SEQUENCING.title.equalsIgnoreCase(title);
    }

    public boolean isMethylationMicroarray(String title) {
        return METHYLATION_MICROARRAY.title.equalsIgnoreCase(title) ||
                PLANT_METHYLATION_MICROARRAY.title.equalsIgnoreCase(title) ||
                HUMAN_METHYLATION_MICROARRAY.title.equalsIgnoreCase(title) ||
                ANIMAL_METHYLATION_MICROARRAY.title.equalsIgnoreCase(title) ||
                CELL_LINE_METHYLATION_MICROARRAY.title.equalsIgnoreCase(title);
    }
}
