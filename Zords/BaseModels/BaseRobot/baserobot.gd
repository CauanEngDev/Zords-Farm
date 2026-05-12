extends "res://Zords/BaseModels/basezord.gd"

@export_group("Informações Base")
@export var display_name: String
@export var energy: int
const SPEED: float = 200.0

var level: int = 1
var target_position = Vector2()


func move_to(pos: Vector2) -> void:
	target_position = pos
	desselect()

func _ready() -> void:
	add_to_group("zords")
	position = Vector2(100, 100)
	target_position = position

func _process(delta: float) -> void:
	if global_position.distance_to(target_position) > 5.0:
		if velocity.x < 0:
			$AnimatedSprite2D.flip_h = true
		elif velocity.x > 0:
			$AnimatedSprite2D.flip_h = false

		$AnimatedSprite2D.play("Zord_Walking")
	else:
		velocity = Vector2.ZERO
		$AnimatedSprite2D.play("Zord_Idle")

func _physics_process(delta: float) -> void:
	if global_position.distance_to(target_position) > 5.0:
		var direction = global_position.direction_to(target_position)
		velocity = direction * SPEED
	move_and_slide()
