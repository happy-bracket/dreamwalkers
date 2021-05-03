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
    local registry = invoker:getRegistry()
    if not registry:has("test_dummy") then
        local spawner = invoker:getSpawner()
        local spawnXy = position_mapper:get(registry:get("dummy_spawner")):getXy()
        local dummy = spawner:spawn(2.0, 1.6, spawnXy, 10.0, 500.0)
        local identity = luajava.newInstance(identity_component, "test_dummy")
        local vitality = luajava.newInstance(vitality_component, 30, 10, 0)
        dummy:add(identity)
        dummy:add(vitality)
        spawner:makeInteractive(dummy, "dummy")
        spawner:addInteraction(dummy, "dummyCallback", "dummy prompt", 2.0)
    end
end

function update(delta)
end
