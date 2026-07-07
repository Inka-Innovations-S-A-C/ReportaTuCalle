INSERT INTO category.categories (name, description, marker_color, algorithm_type, is_active, created_at, updated_at)
VALUES
  ('Bache / Pavimento dañado',   'Huecos o grietas en el asfalto que representan peligro.',           '#EF4444', 'ROUTING',      TRUE, NOW(), NOW()),
  ('Basura acumulada',           'Acumulación de residuos en vía pública o espacios comunes.',        '#F97316', 'ROUTING',      TRUE, NOW(), NOW()),
  ('Fuga de agua',               'Pérdida de agua en tuberías o conexiones visibles en la vía.',    '#3B82F6', 'FLOW',         TRUE, NOW(), NOW()),
  ('Poste / Alumbrado público',  'Luminaria apagada, poste caído o con riesgo eléctrico.',            '#EAB308', 'CONNECTIVITY', TRUE, NOW(), NOW()),
  ('Semáforo averiado',          'Semáforo sin funcionar o con señal incorrecta.',                    '#A855F7', 'CONNECTIVITY', TRUE, NOW(), NOW()),
  ('Árbol caído / Poda urgente', 'Árbol o rama que obstruye la vía o representa riesgo.',             '#22C55E', 'NONE',         TRUE, NOW(), NOW()),
  ('Vereda / Acera deteriorada', 'Rotura o hundimiento de la acera que dificulta el tránsito.',      '#F59E0B', 'ROUTING',      TRUE, NOW(), NOW()),
  ('Otro',                       'Incidencia que no encaja en las categorías anteriores.',            '#6B7280', 'NONE',         TRUE, NOW(), NOW());