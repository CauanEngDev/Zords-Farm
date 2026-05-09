extends CharacterBody2D

@export_group("Informações Base")
@export var display_name: String
@export var energy: int
@export var speed: float = 200.0

signal selected_zord(data_of_zord: Array)

var level: int = 1
var selected: bool = false
var target_position = Vector2()


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

func move_to(pos: Vector2) -> void:
	target_position = pos
	desselect()

func _ready() -> void:
	add_to_group("zords")
	position = Vector2(100, 100)
	target_position = position

func _process(delta: float) -> void:
	if global_position.distance_to(target_position) > 5.0:
		$AnimatedSprite2D.play("Zord_Walking")
	else:
		velocity = Vector2.ZERO
		$AnimatedSprite2D.play("Zord_Idle")

func _physics_process(delta: float) -> void:
	if global_position.distance_to(target_position) > 5.0:
		var direction = global_position.direction_to(target_position)
		velocity = direction * speed
	move_and_slide()
