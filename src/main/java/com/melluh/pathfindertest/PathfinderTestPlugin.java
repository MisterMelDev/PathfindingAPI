package com.melluh.pathfindertest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import com.melluh.pathfindingapi.Path;
import com.melluh.pathfindingapi.PathNode;
import com.melluh.pathfindingapi.Pathfinder;

public class PathfinderTestPlugin extends JavaPlugin {

	private Location origin;
	private Location goal;
	private Pathfinder pathfinder;
	
	@Override
	public void onEnable() {
		this.pathfinder = new Pathfinder.Builder()
				.setMaxNodeVisits(2000)
				//.setMoveDiagonally(false)
				.build();
		
		this.origin = this.loadLocation("origin");
		this.goal = this.loadLocation("goal");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be used by players");
			return true;
		}
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(ChatColor.RED + "Specify a subcommand");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("run")) {
			if(origin == null) {
				player.sendMessage(ChatColor.RED + "Start position not set");
				return true;
			}
			
			if(goal == null) {
				player.sendMessage(ChatColor.RED + "End position not set");
				return true;
			}
			
			long start = System.nanoTime();
			
			Optional<Path> optPath = pathfinder.calculatePath(origin, goal);
			
			long end = System.nanoTime();
			player.sendMessage("Processing took " + String.format("%.2f", ((end - start) / 1000000f)) + "ms");
			
			if(!optPath.isPresent()) {
				player.sendMessage(ChatColor.RED + "No path found");
				return true;
			}
			
			Path path = optPath.get();
			player.sendMessage(ChatColor.GREEN + "Path found! " + path.getNodes().size() + " nodes");
			
			new BukkitRunnable() {
				int count = 0;
				
				@Override
				public void run() {
					for(PathNode node : path.getNodes()) {
						Location loc = node.getPosition().toLocation(origin.getWorld());
						loc.getWorld().spawnParticle(Particle.FLAME, loc.add(0.5, 0, 0.5), 0, 0, 0, 0, 0, null, true);
					}
					
					count++;
					if(count >= 5)
						this.cancel();
				}
			}.runTaskTimer(this, 0, 10);
			
			return true;
		}
		
		if(args[0].equalsIgnoreCase("benchmark")) {
			if(origin == null) {
				player.sendMessage(ChatColor.RED + "Start position not set");
				return true;
			}
			
			if(goal == null) {
				player.sendMessage(ChatColor.RED + "End position not set");
				return true;
			}
			
			long start = System.nanoTime();
			
			for(int i = 0; i < 100; i++) {
				pathfinder.calculatePath(origin, goal);
			}
		
			long end = System.nanoTime();
			player.sendMessage("Processing took " + String.format("%.2f", ((end - start) / 1000000f)) + "ms (" + String.format("%.2f", ((end - start) / 1000000f / 100f)) + " ms per path)");
			
			return true;
		}
		
		if(args[0].equalsIgnoreCase("pos1")) {
			origin = player.getLocation();
			this.saveLocation("origin", origin);
			player.sendMessage(ChatColor.GREEN + "Origin set");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("pos2")) {
			goal = player.getLocation();
			this.saveLocation("goal", goal);
			player.sendMessage(ChatColor.GREEN + "Goal set");
			return true;
		}
		
		player.sendMessage(ChatColor.RED + "Invalid subcommand");
		return true;
	}
	
	private void saveLocation(String key, Location location) {
		this.getConfig().set(key + ".world", location.getWorld().getName());
		this.getConfig().set(key + ".x", location.getBlockX());
		this.getConfig().set(key + ".y", location.getBlockY());
		this.getConfig().set(key + ".z", location.getBlockZ());
		this.saveConfig();
	}
	
	private Location loadLocation(String key) {
		if(!this.getConfig().isConfigurationSection(key))
			return null;
		
		World world = Bukkit.getWorld(this.getConfig().getString(key + ".world"));
		return new Location(world, this.getConfig().getInt(key + ".x"), this.getConfig().getInt(key + ".y"), this.getConfig().getInt(key + ".z"));
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			List<String> options = new ArrayList<>();
			StringUtil.copyPartialMatches(args[0], Arrays.asList("run", "pos1", "pos2"), options);
			Collections.sort(options);
			return options;
		}
		
		return Collections.emptyList();
	}
	
}
