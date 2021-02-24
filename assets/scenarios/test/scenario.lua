require "scripting_utils/components"

function init(invoker)
    local level = invoker:getInteractor():loadLevel("observatory")
    local spawn_point = invoker:getRegistry():get("spawn_point")
    local xy = position_mapper:get(spawn_point):getXy()

    local luna_comp = luajava.newInstance(luna_component)
    local aerial_comp = luajava.newInstance(aerial_component)
    local look_comp = luajava.newInstance(look_component)

    local luna = invoker:getSpawner():spawn(1.8, 1.7, xy, 7.5)
    luna:add(luna_comp)
    luna:add(aerial_comp)
    luna:add(look_comp)
    invoker:getSpawner():equip(luna)

    local test_collision_entity = invoker:getRegistry():get("test_collision")
    local on_collision = luajava.newInstance(on_collision_start_component, "test_spawn")
    test_collision_entity:add(on_collision)
end

function test_spawn(invoker)
    local registry = invoker:getRegistry()
    if not registry:has("test_dummy") then
        local spawnXy = position_mapper:get(registry:get("dummy_spawner")):getXy()
        local dummy = invoker:getSpawner():spawn(0.8, 1.6, spawnXy, 0.0)
        local identity = luajava.newInstance(identity_component, "test_dummy")
        local vitality = luajava.newInstance(vitality_component, 30, 10, 20)
        dummy:add(identity)
        dummy:add(vitality)
    end
end

function update(delta)
end
