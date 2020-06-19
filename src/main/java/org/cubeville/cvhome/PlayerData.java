/*
 * CVHome Bukkit plugin for Minecraft
 * Copyright (C) 2017-2018,2020  Matt Ciolkosz (https://github.com/mciolkosz)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cubeville.cvhome;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("PlayerData")
class PlayerData implements ConfigurationSerializable {
	
	private final String name;
	private final UUID uniqueId;
	
	PlayerData(String name, UUID uniqueId) {
		this.name = name;
		this.uniqueId = uniqueId;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> config = new ConcurrentHashMap<String, Object>();
		config.put("name", name);
		config.put("unique_id", uniqueId.toString());
		return config;
	}
	
	public static PlayerData deserialize(Map<String, Object> config) {
		if(config == null) { throw new IllegalArgumentException("PlayerData config cannot be null."); }
		return new PlayerData((String) config.get("name"), UUID.fromString((String) config.get("unique_id")));
	}
	
	String getName() {
		return name;
	}
	
	UUID getUniqueId() {
		return uniqueId;
	}
}
