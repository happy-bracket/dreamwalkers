
luajava.bindClass("ru.substancial.dreamwalkers.utilities.ComponentMapperFactoryForLua")
local cm_factory = luajava.newInstance("ru.substancial.dreamwalkers.utilities.ComponentMapperFactoryForLua")

local comp_package = "ru.substancial.dreamwalkers.ecs.component."

local position_class = luajava.bindClass(comp_package .. "PositionComponent")
local aerial_class = luajava.bindClass(comp_package .. "AerialComponent")
local body_class = luajava.bindClass(comp_package .. "BodyComponent")
local look_class = luajava.bindClass(comp_package .. "LookComponent")
local luna_class = luajava.bindClass(comp_package .. "LunaComponent")
local movement_class = luajava.bindClass(comp_package .. "MovementComponent")

local position_mapper = cm_factory:getFor(position_class)

luajava.bindClass("java.util.HashSet")

function init(interactor, savefile, world, spawner)
    local level = interactor:loadLevel("observatory")
    local spawn_point = level:getEntityById("spawn_point")
    local xy = position_mapper:get(spawn_point):getXy()

    local components_set = luajava.newInstance("java.util.HashSet")

    local luna_comp = luajava.newInstance(comp_package .. "LunaComponent")
    local aerial_comp = luajava.newInstance(comp_package .. "AerialComponent")
    local look_comp = luajava.newInstance(comp_package .. "LookComponent")

    local luna = spawner:spawnWithHitbox(1.8, 1.7, xy, 7.5)
    luna:add(luna_comp)
    luna:add(aerial_comp)
    luna:add(look_comp)
end