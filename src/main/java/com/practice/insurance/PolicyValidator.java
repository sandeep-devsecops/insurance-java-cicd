package com.practice.insurance;
public class PolicyValidator {
 public static boolean valid(String name, String type, double premium) {
  return name!=null && !name.trim().isEmpty() && type!=null &&
   (type.equals("Health") || type.equals("Life") || type.equals("Vehicle")) &&
   Double.isFinite(premium) && premium>0;
 }
}