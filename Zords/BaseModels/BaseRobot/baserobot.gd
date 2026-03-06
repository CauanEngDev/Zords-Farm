extends CharacterBody2D

@export_group("Informações Base")
@export var display_name: String
@export var energy: int

signal selected_zord(data_of_zord: Array)

var level: int = 1
var selected: bool = false
var target_position = Vector2()
var click_position = Vector2()


func select() -> void:
	if selected:
		return
	selected = true
	selected_zord.emit(self)
	
func desselect() -> void:
	if !selected:
		return
	selected = false
	
## Função que verifica clique no Zord e marca como selecionado
#func _input_event(viewport: Viewport, event: InputEvent, shape_idx: int) -> void:
	## Verifica se o evento do 'input' é um clique de 'mouse'
	#if event is InputEventMouseButton:
		## Verifica se é o botão esquerdo e se ele foi pressionado
		#if event.button_index == MOUSE_BUTTON_LEFT and event.pressed:
			#print("Zord clicado ", display_name)
			#select()
			#get_viewport().set_input_as_handled()
#
## Função que verifica se o jogador clicou em 'nada'
#func _unhandled_input(event: InputEvent) -> void:
	#if event is InputEventMouseButton and event.button_index == MOUSE_BUTTON_LEFT and event.is_pressed():
		#print("Zord desselecionado ", display_name)
		#desselect()

func _ready() -> void:
	position = Vector2(100, 100)

func _process(delta: float) -> void:
	pass
