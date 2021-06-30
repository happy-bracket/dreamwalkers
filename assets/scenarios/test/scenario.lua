require "scripting_utils/components"

function init(invoker)
    local level = invoker:getInteractor():loadLevel("observatory")
end

function on_level_ready(invoker)
    local spawn_point = invoker:getRegistry():get("spawn_point")
    local xy = position_mapper:get(spawn_point):getXy()

    invoker:getSpawner():spawnLuna(xy)

    local test_collision_entity = invoker:getRegistry():get("test_collision")
    local on_collision = luajava.newInstance(on_collision_start_component, "test_spawn")
    test_collision_entity:add(on_collision)
end

function test_spawn(invoker)
    local lunaEntity = invoker:getRegistry():get("Luna")
    local prism = prismatic_mapper:get(lunaEntity)
    prism:damageFor(30.0)
end

function update(delta)
end
