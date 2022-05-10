package model;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;

public class Item {

    private final int id;
    private String name;
    private BigDecimal price;
    private int stock;

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
        price = new BigDecimal(0);
        stock = 0;
    }

    public Item(int id, String name, String price) {
        this(id, name);
        this.price = new BigDecimal(price);
        this.stock = 0;
    }

    public Item(int id, String name, String price, int stock) {
        this(id, name, price);
        this.stock = stock;
    }

    public Item(ResultSet resultSet) {
        final Field[] attributes = Item.class.getDeclaredFields();

        try {
            id = resultSet.getInt(attributes[0].getName());
            name = resultSet.getString(attributes[1].getName());
            int price = resultSet.getInt((attributes[2].getName()));
            this.price = new BigDecimal(price / 100 + "." + price % 100);
            stock = resultSet.getInt(attributes[3].getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Item(Matcher matcher) {
        id = -1; // id is not used
        // group(1) is the CREATE command
        name = matcher.group(2);
        price = new BigDecimal(matcher.group(3));
        stock = Integer.parseInt(matcher.group(4));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(String priceAsDecimalString) {
        double priceAsDouble = Double.parseDouble(priceAsDecimalString);
        String priceAsDecimal = String.format("%.2f", priceAsDouble);
        this.price = new BigDecimal(priceAsDecimal);
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // TODO?: convert to and from JSONObject

    // https://stackoverflow.com/questions/3333974/how-to-loop-over-a-class-attributes-in-java
    public static String getAttributeNamesExceptId() {
        final Field[] attributes = Item.class.getDeclaredFields();
        final Field[] attributesExceptId = Arrays.copyOfRange(attributes, 1, attributes.length);

        StringBuilder commaSeparatedAttributes = new StringBuilder();
        for (int i = 0; i < attributesExceptId.length; i++) {
            commaSeparatedAttributes.append(attributesExceptId[i].getName());
            if (i != attributesExceptId.length - 1) {
                commaSeparatedAttributes.append(", ");
            }
        }
        return commaSeparatedAttributes.toString();
    }

    // FIXME: used for database insertion
    //  probably should use string array instead
    public String getAttributeValuesExceptId() {
        return "'" + name + "'," + price.scaleByPowerOfTen(2).intValue() + ", " + stock;
    }

    public static String[] getAttributeNamesAsArray() {
        Field[] attributes = Item.class.getDeclaredFields();
        Field[] attributesExceptId = Arrays.copyOfRange(attributes, 1, attributes.length);
        String[] attributeNames = new String[attributesExceptId.length];

        for (int i = 0; i < attributesExceptId.length; i++) {
            attributeNames[i] = attributesExceptId[i].getName();
        }
        return attributeNames;
    }

    public String[] getValuesAsArray() {
        return new String[] { name, price.toString(), String.valueOf(stock) };
    }

    public String getAttributeNameValueListExceptId() {
        return "name = " + "'" + name + "', price = " +
                price.scaleByPowerOfTen(2).intValue() + ", stock = " + stock;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof Item item)) {
            return false;
        }

        return id == item.getId() &&
                name.equals(item.getName()) &&
                price.compareTo(item.getPrice()) == 0 &&
                stock == item.getStock();
    }

    @Override
    public String toString() {
        return id + ", '" + name + "', " + price + ", " + stock;
    }
}