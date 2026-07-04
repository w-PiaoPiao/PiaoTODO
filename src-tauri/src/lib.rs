use std::fs;
use std::path::PathBuf;
use std::sync::Mutex;
use std::time::Instant;
use tauri::Manager;
use tauri::tray::{MouseButton, MouseButtonState, TrayIconBuilder, TrayIconEvent};
use tauri_plugin_global_shortcut::GlobalShortcutExt;
use tauri_plugin_global_shortcut::{Code, Modifiers, Shortcut, ShortcutState};

struct AppState {
    last_shown: Mutex<Instant>,
}

fn get_data_path(app: &tauri::AppHandle) -> PathBuf {
    let app_dir = app
        .path()
        .app_data_dir()
        .expect("failed to get app data dir");
    fs::create_dir_all(&app_dir).ok();
    app_dir.join("data.json")
}

#[tauri::command]
fn load_data(app: tauri::AppHandle) -> Result<String, String> {
    let path = get_data_path(&app);
    fs::read_to_string(&path).map_err(|e| format!("failed to read data: {}", e))
}

#[tauri::command]
fn save_data(app: tauri::AppHandle, data: String) -> Result<(), String> {
    let path = get_data_path(&app);
    fs::write(&path, &data).map_err(|e| format!("failed to save data: {}", e))
}

fn toggle_window(app: &tauri::AppHandle) {
    if let Some(window) = app.get_webview_window("main") {
        if window.is_visible().unwrap_or(false) {
            window.hide().ok();
        } else {
            #[cfg(target_os = "macos")]
            {
                if let Ok(Some(monitor)) = window.current_monitor() {
                    let size = monitor.size();
                    let window_size = window.outer_size().unwrap_or(tauri::PhysicalSize::new(360, 480));
                    let x = size.width as i32 - window_size.width as i32 - 20;
                    let y = 30;
                    window.set_position(tauri::Position::Physical(tauri::PhysicalPosition::new(x, y))).ok();
                }
            }

            if let Some(state) = app.try_state::<AppState>() {
                *state.last_shown.lock().unwrap() = Instant::now();
            }

            window.show().ok();
            window.set_focus().ok();
        }
    }
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .manage(AppState {
            last_shown: Mutex::new(Instant::now()),
        })
        .plugin(tauri_plugin_shell::init())
        .plugin(
            tauri_plugin_global_shortcut::Builder::new()
                .with_handler(|app, shortcut, event| {
                    if event.state == ShortcutState::Pressed
                        && shortcut.matches(Modifiers::META | Modifiers::SHIFT, Code::KeyT)
                    {
                        toggle_window(app);
                    }
                })
                .build(),
        )
        .setup(|app| {
            TrayIconBuilder::new()
                .icon(app.default_window_icon().unwrap().clone())
                .tooltip("LiteToDo")
                .on_tray_icon_event(|tray, event| {
                    match event {
                        TrayIconEvent::Click {
                            button: MouseButton::Left,
                            button_state: MouseButtonState::Up,
                            ..
                        } => {
                            toggle_window(tray.app_handle());
                        }
                        _ => {}
                    }
                })
                .build(app)?;

            app.global_shortcut()
                .register(Shortcut::new(Some(Modifiers::META | Modifiers::SHIFT), Code::KeyT))?;

            if let Some(window) = app.get_webview_window("main") {
                window.hide().ok();
            }

            Ok(())
        })
        .on_window_event(|window, event| {
            if let tauri::WindowEvent::Focused(false) = event {
                if let Some(state) = window.try_state::<AppState>() {
                    let elapsed = state.last_shown.lock().unwrap().elapsed();
                    if elapsed.as_millis() > 200 {
                        window.hide().ok();
                    }
                }
            }
        })
        .invoke_handler(tauri::generate_handler![load_data, save_data])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
