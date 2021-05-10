package com.app.orion_driver.classes;

import java.util.HashMap;
import java.util.Map;

public class ProductOptions {

    public Map<String, String> genderMap = new HashMap<>();
    public static final String[] categoryItems = {
            "Accessories",
            "Activewear",
            "Baby Products",
            "Bags",
            "Clothing",
            "Foodwear",
            "Perfumes",
            "Shoes",
            "Stationery",
            "Sports",
            "Toiletry",
            "Toys",
            "Others",
    };

    public static final String[] genderItems = {
            "Men(Adult)",
            "Women(Adult)",
            "Boys(Kids)",
            "Girls(Kids)",
            "Unisex(Adult)",
            "Unisex(Kids)",
            "Unisex(All)",
    };

    public String getGenderKey(String key){
        genderMap.put(genderItems[0], "men");
        genderMap.put(genderItems[1], "women");
        genderMap.put(genderItems[2], "men kids");
        genderMap.put(genderItems[3], "women kids");
        genderMap.put(genderItems[4], "men and women");
        genderMap.put(genderItems[5], "men and women kids");
        genderMap.put(genderItems[6], "men and women");

        return genderMap.get(key);
    }

    public static final String[] subcategoryItems = {
            "Accessories",
            "Activewear",
            "Backpacks",
            "Bedspread",
            "Belts",
            "Bench",
            "Blanket",
            "Bookcase",
            "Boots",
            "Candle",
            "Candlestick",
            "Chair",
            "Coffee Machine",
            "Coffee Table",
            "Couch",
            "Cup",
            "Desk",
            "Denim",
            "Dresses & Skirts",
            "Flats",
            "Glass",
            "Handbags",
            "Health & Beauty",
            "Heels",
            "Intimates & Loungewear",
            "Jackets",
            "Jewelry & Watches",
            "Lamp",
            "Leggings",
            "Maternity",
            "Mirror",
            "Outerwear",
            "Pants",
            "Pants & Shorts",
            "Picture Frame",
            "Pillow",
            "Pillow Case",
            "Plate",
            "Polos & Tees",
            "Rug",
            "Sandals",
            "Sheet",
            "Shirts",
            "Shirts & Sweaters",
            "Shoes",
            "Shorts & Swimwear",
            "Skirts",
            "Socks, Underwear & Sleepwear",
            "Sofa",
            "Suits & Blazers",
            "Suits & Sportcoats",
            "Sweaters & Hoodies",
            "Swimwear",
            "Table",
            "Table Cloth",
            "Tea Kettle",
            "Vanity",
            "Vase",
            "Wallpaper",
            "Workout Shirts",
            "Yoga Pants",
            "Others"
    };


}
