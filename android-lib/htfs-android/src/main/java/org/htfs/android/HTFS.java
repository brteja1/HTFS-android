package org.htfs.android;

import android.content.Context;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import java.util.ArrayList;
import java.util.List;

/**
 * Java wrapper for the HTFS (Hierarchical Tagging File System) python library.
 */
public class HTFS {
    private final PyObject pyHtfsInstance;

    /**
     * Initializes Python runtime if not already started, and instantiates the HTFS engine.
     *
     * @param context Android context
     * @param tagfsBoundary Absolute path to the directory containing .tagfs.db and .tagfs.ttl
     */
    public HTFS(Context context, String tagfsBoundary) {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
        Python py = Python.getInstance();
        PyObject coreModule = py.getModule("htfs.core");
        PyObject htfsClass = coreModule.get("HTFS");
        if (htfsClass == null) {
            throw new IllegalStateException("Could not find HTFS class in htfs.core module");
        }
        this.pyHtfsInstance = htfsClass.call(tagfsBoundary);
    }

    /**
     * Closes the engine, ensuring RDF and SQLite states are flushed/saved.
     */
    public void close() {
        pyHtfsInstance.callAttr("close");
    }

    /**
     * Initializes the DB schemas.
     */
    public void initialize() {
        pyHtfsInstance.callAttr("initialize");
    }

    /**
     * Adds tags to the system. Supports hierarchical syntax (e.g. "Project/Alpha/Reports").
     *
     * @param tags List of tags to add.
     * @return List of newly created atomic tag names.
     */
    public List<String> addTags(List<String> tags) {
        PyObject result = pyHtfsInstance.callAttr("add_tags", tags);
        return pyListToStringList(result);
    }

    /**
     * Renames an existing tag.
     *
     * @param tagName Current tag name.
     * @param newTagName New tag name.
     * @return True if renamed successfully, false otherwise.
     */
    public boolean renameTag(String tagName, String newTagName) {
        PyObject result = pyHtfsInstance.callAttr("rename_tag", tagName, newTagName);
        return result.toBoolean();
    }

    /**
     * Deletes a tag and removes all links to it.
     *
     * @param tagName Tag name to delete.
     * @return True if deleted successfully.
     */
    public boolean delTag(String tagName) {
        PyObject result = pyHtfsInstance.callAttr("del_tag", tagName);
        return result.toBoolean();
    }

    /**
     * Adds a resource under tracking.
     *
     * @param resourceUrl Path/URL to resource.
     * @return The resource ID, or -1 if already tracked.
     */
    public int addResource(String resourceUrl) {
        PyObject result = pyHtfsInstance.callAttr("add_resource", resourceUrl);
        return result.toInt();
    }

    /**
     * Checks if a resource is tracked.
     *
     * @param resourceUrl Path/URL to resource.
     * @return True if tracked.
     */
    public boolean isResourceTracked(String resourceUrl) {
        PyObject result = pyHtfsInstance.callAttr("is_resource_tracked", resourceUrl);
        return result.toBoolean();
    }

    /**
     * Untracks a resource and removes all tags associated with it.
     *
     * @param resourceUrl Path/URL to resource.
     */
    public void delResource(String resourceUrl) {
        pyHtfsInstance.callAttr("del_resource", resourceUrl);
    }

    /**
     * Associates tags with a resource.
     *
     * @param resourceUrl Path/URL of the tracked resource.
     * @param tags List of tag names/paths.
     * @return List of tag names that failed to associate (e.g. hierarchical path not found).
     */
    public List<String> tagResource(String resourceUrl, List<String> tags) {
        PyObject result = pyHtfsInstance.callAttr("tag_resource", resourceUrl, tags);
        return pyListToStringList(result);
    }

    /**
     * Disassociates tags from a resource.
     *
     * @param resourceUrl Path/URL of the resource.
     * @param tags List of tags to remove.
     * @return List of tag names that failed to remove.
     */
    public List<String> untagResource(String resourceUrl, List<String> tags) {
        PyObject result = pyHtfsInstance.callAttr("untag_resource", resourceUrl, tags);
        return pyListToStringList(result);
    }

    /**
     * Updates tracked resource's location/URL.
     *
     * @param resourceUrl Old path/URL.
     * @param targetUrl New path/URL.
     */
    public void moveResource(String resourceUrl, String targetUrl) {
        pyHtfsInstance.callAttr("move_resource", resourceUrl, targetUrl);
    }

    /**
     * Gets resources matching given tags (AND semantics).
     *
     * @param tags List of tags.
     * @return List of resource URLs.
     */
    public List<String> getResourcesByTag(List<String> tags) {
        PyObject result = pyHtfsInstance.callAttr("get_resources_by_tag", tags);
        return pyListToStringList(result);
    }

    /**
     * Gets resources matching a boolean tag query expression (e.g. "(tagA|tagB)&tagC").
     *
     * @param tagsExpr The query expression.
     * @return List of matching resource URLs.
     */
    public List<String> getResourcesByTagExpr(String tagsExpr) {
        PyObject result = pyHtfsInstance.callAttr("get_resources_by_tag_expr", tagsExpr);
        return pyListToStringList(result);
    }

    /**
     * Creates a child-parent hierarchical link between two existing tags.
     *
     * @param tag Child tag name.
     * @param parentTag Parent tag name.
     * @return True if linked successfully.
     */
    public boolean linkTags(String tag, String parentTag) {
        PyObject result = pyHtfsInstance.callAttr("link_tags", tag, parentTag);
        return result.toBoolean();
    }

    /**
     * Gets all tags associated with a resource.
     *
     * @param resourceUrl Path/URL of the resource.
     * @return List of tag names.
     */
    public List<String> getResourceTags(String resourceUrl) {
        PyObject result = pyHtfsInstance.callAttr("get_resource_tags", resourceUrl);
        return pyListToStringList(result);
    }

    /**
     * Gets all tag names or those filtered by tags closure.
     *
     * @param tags Optional tag filter list.
     * @return List of tags.
     */
    public List<String> getTagsList(List<String> tags) {
        PyObject result = pyHtfsInstance.callAttr("get_tags_list", tags);
        return pyListToStringList(result);
    }

    /**
     * Exports the HTFS tagging relationship graph in Graphviz DOT format.
     *
     * @return DOT format string.
     */
    public String exportGraphvizDot() {
        PyObject result = pyHtfsInstance.callAttr("export_graphviz_dot");
        return result.toString();
    }

    private List<String> pyListToStringList(PyObject pyList) {
        List<String> list = new ArrayList<>();
        if (pyList != null) {
            List<PyObject> items = pyList.asList();
            for (PyObject item : items) {
                list.add(item.toString());
            }
        }
        return list;
    }
}
