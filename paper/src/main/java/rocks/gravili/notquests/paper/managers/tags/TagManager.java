package rocks.gravili.notquests.paper.managers.tags;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.managers.data.Category;
import rocks.gravili.notquests.paper.structs.QuestPlayer;
import rocks.gravili.notquests.paper.structs.actions.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TagManager {
    private final NotQuests main;
    private final HashMap<String, Tag> identifiersAndTags;

    NamespacedKey booleanTagsNestedPDCKey, integerTagsNestedPDCKey, floatTagsNestedPDCKey, doubleTagsNestedPDCKey, stringTagsNestedPDC;

    public TagManager(final NotQuests main){
        this.main = main;
        this.identifiersAndTags = new HashMap<>();
        booleanTagsNestedPDCKey = new NamespacedKey(main.getMain(), "notquests_tags_boolean");
        integerTagsNestedPDCKey = new NamespacedKey(main.getMain(), "notquests_tags_integer");
        floatTagsNestedPDCKey = new NamespacedKey(main.getMain(), "notquests_tags_float");
        doubleTagsNestedPDCKey = new NamespacedKey(main.getMain(), "notquests_tags_double");
        stringTagsNestedPDC = new NamespacedKey(main.getMain(), "notquests_tags_string");

        loadTags();
    }

    //TODO: test if hashmap => bytestream serialization is faster
    public void onJoin(final QuestPlayer questPlayer, final Player player){
        if(questPlayer.getTags().size() > 0){
            return;
        }
        PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
        PersistentDataContainer booleanTagsContainer = persistentDataContainer.get(booleanTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER);
        PersistentDataContainer integerTagsContainer = persistentDataContainer.get(integerTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER);
        PersistentDataContainer floatTagsContainer = persistentDataContainer.get(integerTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER);
        PersistentDataContainer doubleTagsContainer = persistentDataContainer.get(integerTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER);
        PersistentDataContainer stringTagsContainer = persistentDataContainer.get(stringTagsNestedPDC, PersistentDataType.TAG_CONTAINER);

        if(booleanTagsContainer != null){
            for(NamespacedKey key : booleanTagsContainer.getKeys()){
                questPlayer.setTag(key.getKey(), booleanTagsContainer.get(key, PersistentDataType.BYTE)!=0);
            }
        }

        if(integerTagsContainer != null){
            for(NamespacedKey key : integerTagsContainer.getKeys()){
                questPlayer.setTag(key.getKey(), integerTagsContainer.get(key, PersistentDataType.INTEGER));
            }
        }

        if(floatTagsContainer != null){
            for(NamespacedKey key : floatTagsContainer.getKeys()){
                questPlayer.setTag(key.getKey(), floatTagsContainer.get(key, PersistentDataType.FLOAT));
            }
        }

        if(doubleTagsContainer != null){
            for(NamespacedKey key : doubleTagsContainer.getKeys()){
                questPlayer.setTag(key.getKey(), doubleTagsContainer.get(key, PersistentDataType.DOUBLE));
            }
        }

        if(stringTagsContainer != null){
            for(NamespacedKey key : stringTagsContainer.getKeys()){
                questPlayer.setTag(key.getKey(), stringTagsContainer.get(key, PersistentDataType.STRING));
            }
        }

    }

    public void onQuit(final QuestPlayer questPlayer, final Player player){
        PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
        PersistentDataContainer booleanTagsContainer = persistentDataContainer.get(booleanTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER);
        PersistentDataContainer integerTagsContainer = persistentDataContainer.get(integerTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER);
        PersistentDataContainer floatTagsContainer = persistentDataContainer.get(floatTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER);
        PersistentDataContainer doubleTagsContainer = persistentDataContainer.get(doubleTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER);
        PersistentDataContainer stringTagsContainer = persistentDataContainer.get(stringTagsNestedPDC, PersistentDataType.TAG_CONTAINER);

        for(String tagIdentifier : questPlayer.getTags().keySet()){
            Object tagValue = questPlayer.getTag(tagIdentifier);
            if(tagValue instanceof Boolean booleanTagValue){
                if(booleanTagsContainer == null){
                    booleanTagsContainer = persistentDataContainer.getAdapterContext().newPersistentDataContainer();
                }
                booleanTagsContainer.set(new NamespacedKey(main.getMain(), tagIdentifier), PersistentDataType.BYTE, (byte)(booleanTagValue ? 1 : 0));

                persistentDataContainer.set(booleanTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER, booleanTagsContainer);  //TODO: Check if needed
            }else if(tagValue instanceof Integer integerTagValue){
                if(integerTagsContainer == null){
                    integerTagsContainer = persistentDataContainer.getAdapterContext().newPersistentDataContainer();
                }
                integerTagsContainer.set(new NamespacedKey(main.getMain(), tagIdentifier), PersistentDataType.INTEGER, integerTagValue);

                persistentDataContainer.set(integerTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER, integerTagsContainer); //TODO: Check if needed
            }else if(tagValue instanceof Float floatValue){
                if(floatTagsContainer == null){
                    floatTagsContainer = persistentDataContainer.getAdapterContext().newPersistentDataContainer();
                }
                floatTagsContainer.set(new NamespacedKey(main.getMain(), tagIdentifier), PersistentDataType.FLOAT, floatValue);

                persistentDataContainer.set(integerTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER, floatTagsContainer); //TODO: Check if needed
            }else if(tagValue instanceof Double doubleValue){
                if(doubleTagsContainer == null){
                    doubleTagsContainer = persistentDataContainer.getAdapterContext().newPersistentDataContainer();
                }
                doubleTagsContainer.set(new NamespacedKey(main.getMain(), tagIdentifier), PersistentDataType.DOUBLE, doubleValue);

                persistentDataContainer.set(integerTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER, doubleTagsContainer); //TODO: Check if needed
            }else if(tagValue instanceof String stringTagValue){
                if(stringTagsContainer == null){
                    stringTagsContainer = persistentDataContainer.getAdapterContext().newPersistentDataContainer();
                }
                stringTagsContainer.set(new NamespacedKey(main.getMain(), tagIdentifier), PersistentDataType.STRING, stringTagValue);

                persistentDataContainer.set(integerTagsNestedPDCKey, PersistentDataType.TAG_CONTAINER, stringTagsContainer); //TODO: Check if needed
            }
        }
    }

    public final Tag getTag(final String tagIdentifier){
        return identifiersAndTags.get(tagIdentifier);
    }

    public void addTag(final Tag newTag){
        if (identifiersAndTags.get(newTag.getTagName()) != null) {
            return;
        }

        identifiersAndTags.put(newTag.getTagName(), newTag);

        newTag.getCategory().getTagsConfig().set("tags." + newTag.getTagName() + ".tagType", newTag.getTagType().name());

        newTag.getCategory().saveCategoryConfig();
    }

    public final Collection<Tag> getTags(){
        return identifiersAndTags.values();
    }


    public void loadTags() {
        ArrayList<String> categoriesStringList = new ArrayList<>();
        for (final Category category : main.getDataManager().getCategories()) {
            categoriesStringList.add(category.getCategoryFullName());
        }
        main.getLogManager().info("Scheduled Tags Data load for following categories: <highlight>" + categoriesStringList.toString() );

        for (final Category category : main.getDataManager().getCategories()) {
            loadTags(category);
            main.getLogManager().info("Loading tags for category <highlight>" + category.getCategoryFullName());
        }
    }

    public void loadTags(final Category category) {
        //First load from tags.yml:
        if(category.getTagsConfig() == null){
            main.getLogManager().severe("Error: Cannot load tags of category <highlight>" + category.getCategoryFullName() + "</highlight>, because it doesn't have a tags config. This category has been skipped.");
            return;
        }

        final ConfigurationSection tagsConfigurationSection = category.getTagsConfig().getConfigurationSection("tags");
        if (tagsConfigurationSection != null) {
            for (final String tagIdentifier : tagsConfigurationSection.getKeys(false)) {
                if (identifiersAndTags.get(tagIdentifier) != null) {
                    main.getDataManager().disablePluginAndSaving("Plugin disabled, because there was an error while loading tags.yml tag data: The action " + tagIdentifier + " already exists.");
                    return;
                }
                main.getLogManager().info("Loading tag <highlight>" + tagIdentifier);

                String tagTypeString = tagsConfigurationSection.getString(tagIdentifier + ".tagType", "");

                Tag tag = new Tag(main, tagIdentifier, TagType.valueOf(tagTypeString));
                tag.setCategory(category);


                if (tag != null) {
                    identifiersAndTags.put(tagTypeString, tag);
                } else {
                    main.getDataManager().disablePluginAndSaving("Plugin disabled, because there was an error while loading tags.yml tag data.");
                }
            }
        }

    }
}