require "scripting_utils/components"

function init(invoker)
    local level = invoker:getInteractor():loadLevel("observatory")
    local spawn_point = invoker:getRegistry():get("spawn_point")
    local xy = position_mapper:get(spawn_point):getXy()

    local luna_comp = luajava.newInstance(luna_component)
    local aerial_comp = luajava.newInstance(aerial_component, 0, 10.0, 500.0)
    local look_comp = luajava.newInstance(look_component)
    local identity_comp = luajava.newInstance(identity_component, "Luna")
    local dash_comp = luajava.newInstance(dash_component, 1.0, 0.0)

    local luna = invoker:getSpawner():spawn(1.8, 1.7, xy, 7.5, 500.0)
    luna:add(luna_comp)
    luna:add(aerial_comp)
    luna:add(look_comp)
    luna:add(identity_comp)
    luna:add(dash_comp)
    invoker:getSpawner():equip(luna, "armory/sword.tmx")

    local test_collision_entity = invoker:getRegistry():get("test_collision")
    local on_collision = luajava.newInstance(on_collision_start_component, "test_spawn")
    test_collision_entity:add(on_collision)
end

function test_spawn(invoker)
    invoker:getInteractor():gameOver("", "Bad Dream Ending", "You know that feeling when you die in a dream? This is the one.")
end

function update(delta)
end
