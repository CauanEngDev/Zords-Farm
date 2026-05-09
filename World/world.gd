extends Node2D

func _unhandled_input(event: InputEvent) -> void:
	if event is InputEventMouseButton and event.button_index == MOUSE_BUTTON_LEFT and event.pressed:
		var mouse_pos = get_global_mouse_position()
		
		# Verifica se clicou em algum Zord
		var space = get_world_2d().direct_space_state
		var query = PhysicsPointQueryParameters2D.new()
		query.position = mouse_pos
		var results = space.intersect_point(query)
		
		for result in results:
			if result.collider.is_in_group("zords"):
				result.collider.toggle_select()
				return  # clicou num Zord, para aqui
		
		# Clicou no chão — move ou deseleciona
		for zord in get_tree().get_nodes_in_group("zords"):
			if zord.selected:
				zord.move_to(mouse_pos)
