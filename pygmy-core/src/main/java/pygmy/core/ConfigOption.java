package pygmy.core;

/**
 * This class is used by handlers or endpoints to tell the server which options are
 * used to configure the object.
 */
public class ConfigOption {
    String propertyName;
    String defaultValue;
    boolean isRequired;
    String helpString;

    /**
     * This is used to create an optional property that defaults to null if unspecified.
     *
     * @param propertyName the name of the property.
     * @param helpString   the help string shown to the user.
     */
    public ConfigOption(String propertyName, String helpString) {
        this(propertyName, false, helpString);
    }

    /**
     * This is used to create an optional property that has a supplied default value.
     *
     * @param propertyName the name of the property.
     * @param defaultValue the default value used if this property is unspecified.
     * @param helpString   the help string shown to the user.
     */
    public ConfigOption(String propertyName, String defaultValue, String helpString) {
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.isRequired = false;
        this.helpString = helpString;
    }

    /**
     * This is used to create a required property.  There is no default supplied in the case
     * where required is true.  If you specify it as false it's the same as an optional property
     * with no default.
     *
     * @param propertyName the name of the property.
     * @param required     Used to specify a required property.  True for required, false for optional.
     * @param helpString   the help string shown to the user if nothing is specified.
     */
    public ConfigOption(String propertyName, boolean required, String helpString) {
        this.propertyName = propertyName;
        this.defaultValue = null;
        this.isRequired = required;
        this.helpString = helpString;
    }

    /**
     * This is used to fetch the value of the property.  It's returned as a String.  It will return
     * the default property if it's not specified.
     *
     * @param server the Server object used by the system.
     * @param name   the name of the handler or endpoint instance.
     * @return the value of the property or the default value if it was supplied in the constructor.
     */
    public String getProperty(Server server, String name) {
        String key = propertyName;
        if (name != null) {
            key = name + "." + key;
        }
        String value = server.getProperty(key, defaultValue);
        if (isRequired && value == null) {
            throw new IllegalArgumentException(key + " is a required argument.");
        }
        return value;
    }

    /**
     * This is used to fetch the value of the property as a Boolean.  It will return
     * the default property if it's not specified.
     *
     * @param server the Server object used by the system.
     * @param name   the name of the handler or endpoint instance.
     * @return a Boolean value of the property or the default value if it was supplied in the constructor.
     */
    public Boolean getBoolean(Server server, String name) {
        return new Boolean(getProperty(server, name));
    }

    /**
     * This is used to fetch the value of the property as an Integer.  It will return
     * the default property if it's not specified.
     *
     * @param server the Server object used by the system.
     * @param name   the name of the handler or endpoint instance.
     * @return an Integer value of the property or the default value if it was supplied in the constructor.
     */
    public Integer getInteger(Server server, String name) {
        return new Integer(getProperty(server, name));
    }

    /**
     * This method is used to return a user viewable help string in case an object was misconfigured.
     *
     * @return the user viewable string for misconfigured properties.
     */
    public String toHelp() {
        return propertyName + "\t" + (isRequired ? "Required" : "Optional") + "\t" + defaultValue + "\t" + helpString;
    }

    /**
     * The name of the configuration property that this ConfigOption was constructed with.
     *
     * @return the propery's name.
     */
    public String getName() {
        return propertyName;
    }
}
