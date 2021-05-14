luajava.bindClass("ru.substancial.dreamwalkers.utilities.ComponentMapperFactoryForLua")
local cm_factory = luajava.newInstance("ru.substancial.dreamwalkers.utilities.ComponentMapperFactoryForLua")

local comp_package = "ru.substancial.dreamwalkers.ecs.component."

position_component = comp_package .. "PositionComponent"
aerial_component = comp_package .. "AerialComponent"
body_component = comp_package .. "BodyComponent"
look_component = comp_package .. "LookComponent"
luna_component = comp_package .. "LunaComponent"
movement_component = comp_package .. "TerrainMovementComponent"
vitality_component = comp_package .. "PrismaticComponent"
identity_component = comp_package .. "IdentityComponent"
on_collision_start_component = comp_package .. "OnCollisionStartComponent"
dash_component = comp_package .. "DashComponent"

position_class = luajava.bindClass(position_component)
aerial_class = luajava.bindClass(aerial_component)
body_class = luajava.bindClass(body_component)
look_class = luajava.bindClass(look_component)
luna_class = luajava.bindClass(luna_component)
movement_class = luajava.bindClass(movement_component)
prismatic_class = luajava.bindClass(vitality_component)
identity_class = luajava.bindClass(identity_component)
on_collision_start_class = luajava.bindClass(on_collision_start_component)
dash_class = luajava.bindClass(dash_component)

position_mapper = cm_factory:getFor(position_class)
identity_mapper = cm_factory:getFor(identity_class)
