package com.yellowbkpk.osmnearby;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class IconLookup {

    private static class Rule {
        private Rule(String k, String v) {
            this.key = k;
            this.value = v;
        }

        static Rule n(String k, String v) {
            return new Rule(k, v);
        }

        private String key;
        private String value;
        
        public boolean matches(Map<String,String> tags) {
            return value.equals(tags.get(key));
        }
    }

    private static Map<Rule[], Integer> mappings = new HashMap<IconLookup.Rule[], Integer>();

    static {
        addMapping(R.drawable.transport_fuel, Rule.n("amenity", "fuel"));
        
        addMapping(R.drawable.amenity_firestation2, Rule.n("amenity", "fire_station"));
        
        addMapping(R.drawable.education_university, Rule.n("amenity", "university"));
        addMapping(R.drawable.education_school, Rule.n("amenity", "school"));
        
        addMapping(R.drawable.food_restaurant, Rule.n("amenity", "restaurant"));
        addMapping(R.drawable.food_cafe, Rule.n("amenity", "cafe"));
        addMapping(R.drawable.food_pub, Rule.n("amenity", "pub"));
        addMapping(R.drawable.food_bar, Rule.n("amenity", "bar"));
        addMapping(R.drawable.food_fastfood2, Rule.n("amenity", "fast_food"), Rule.n("cuisine", "burger"));
        addMapping(R.drawable.food_pizza, Rule.n("amenity", "fast_food"), Rule.n("cuisine", "pizza"));
        addMapping(R.drawable.food_fastfood, Rule.n("amenity", "fast_food"));
        
        addMapping(R.drawable.place_of_worship, Rule.n("amenity", "place_of_worship"));
        addMapping(R.drawable.place_of_worship_christian, Rule.n("amenity", "place_of_worship"), Rule.n("religion", "christian"));
        
        addMapping(R.drawable.accommodation_hotel, Rule.n("tourism", "hotel"));
    }

    public static int forTags(Map<String, String> tags) {
        next_mapping:
        for (Entry<Rule[], Integer> entry : mappings.entrySet()) {
            Rule[] rules = entry.getKey();
            for (Rule rule : rules) {
                if(!rule.matches(tags)) {
                    continue next_mapping;
                }
            }
            return entry.getValue();
        }
        return 0;
    }

    private static void addMapping(int iconResource, Rule... rules) {
        mappings.put(rules, iconResource);
    }
}
