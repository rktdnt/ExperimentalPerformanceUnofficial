package ca.fxco.experimentalperformance.memoryDensity;

import ca.fxco.experimentalperformance.ExperimentalPerformance;
import ca.fxco.experimentalperformance.utils.AsmUtils;
import ca.fxco.experimentalperformance.utils.GeneralUtils;
import com.chocohead.mm.api.ClassTinkerers;

import java.util.List;

public class InfoHolderData {

    private static final String ALL_VERSIONS = "*";
    private static final String MINECRAFT_ID = "minecraft";

    private final String targetClassName;
    private final String holderClassName;
    private final List<String> redirectFields;
    private final String modId;
    private final String versionPredicate;

    public InfoHolderData(String targetClassName, String holderClassName, List<String> redirectFields) {
        this(targetClassName, holderClassName, redirectFields, MINECRAFT_ID);
    }

    public InfoHolderData(String targetClassName, String holderClassName,
                           List<String> redirectFields, String modId) {
        this(targetClassName, holderClassName, redirectFields, modId, ALL_VERSIONS);
    }

    public InfoHolderData(String targetClassName, String holderClassName, List<String> redirectFields,
                          String modId, String versionPredicate) {
        if (redirectFields.size() == 0)
            throw new IllegalArgumentException("`redirectFields` must have at least 1 field to redirect!");
        if (redirectFields.size() == 1) // Allow 1 field although it's not recommended
            ExperimentalPerformance.LOGGER.warn("`redirectFields` should have more than 1 field to redirect.");
        this.targetClassName = targetClassName;
        this.holderClassName = holderClassName;
        this.redirectFields = redirectFields;
        this.modId = modId;
        this.versionPredicate = versionPredicate;
    }

    public String getModId() {
        return this.modId;
    }

    public String getversionPredicate() {
        return this.versionPredicate;
    }

    // Override this method to add your own loading logic
    public boolean shouldLoad() {
        return true;
    }

    public void apply() {
        if (!shouldLoad()) return;
        ClassTinkerers.addTransformation(targetClassName, node -> {
            String className = ExperimentalPerformance.VERBOSE ? GeneralUtils.getLastPathPart(targetClassName) : "";
            AsmUtils.removeFieldsContaining(className, node.fields, redirectFields);
            AsmUtils.redirectFieldsToInfoHolder(node.methods, targetClassName, holderClassName, redirectFields);
        });
    }

    //May want to setup a builder if I add any more options
}
