extends CharacterBody2D

signal selected_zord(data_of_zord: Array)

var selected: bool = false

func select() -> void:
	if selected:
		return
	print("Zord selecionado: " + name)
	selected = true
	selected_zord.emit(self)
	
func desselect() -> void:
	if !selected:
		return
	print("Zord deselcionado: " + name)
	selected = false
	
func toggle_select() -> void:
	if selected:
		desselect()
	else:
		select()
		
func _ready() -> void:
	add_to_group("zords")
