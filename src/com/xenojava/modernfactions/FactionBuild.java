package com.xenojava.modernfactions;

import org.bukkit.inventory.ItemStack;

public class FactionBuild {

	private final String name;
	private String schematic_name;
	private ItemStack item;
	private double cost;

	public FactionBuild(String name) {
		this.name = name;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public void setSchematicName(String schematic_name) {
		this.schematic_name = schematic_name;
	}

	public double getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public ItemStack getItem() {
		return item;
	}

	public String getSchematicName() {
		return schematic_name;
	}
}
