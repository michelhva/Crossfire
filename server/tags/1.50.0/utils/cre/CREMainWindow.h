#include <Qt>

#include <QMainWindow>

#include "CREResourcesWindow.h"

class QMdiArea;
class QAction;
class QMenu;
class QLabel;
class CREArtifactWindow;
class CREArchetypeWindow;
class CRETreasureWindow;
class CREAnimationWindow;
class CREFormulaeWindow;
class CREMapInformationManager;

class CREMainWindow : public QMainWindow
{
    Q_OBJECT

    public:
        CREMainWindow();

    signals:
        void updateFilters();
        void updateReports();

    private:
        QMdiArea* myArea;

        void createActions();
        void createMenus();

        QMenu* myOpenMenu;
        QMenu* mySaveMenu;

        QAction* myOpenArtifacts;
        QAction* myOpenArchetypes;
        QAction* myOpenTreasures;
        QAction* myOpenAnimations;
        QAction* myOpenFormulae;
        QAction* myOpenFaces;
        QAction* myOpenMaps;
        QAction* myOpenResources;
        QAction* myOpenExperience;
        QAction* mySaveFormulae;
        QLabel* myMapBrowseStatus;
        CREMapInformationManager* myMapManager;

    protected:
        void closeEvent(QCloseEvent* event);
        void doResourceWindow(DisplayMode mode);

    private slots:
        void onOpenArtifacts();
        void onOpenArchetypes();
        void onOpenTreasures();
        void onOpenAnimations();
        void onOpenFormulae();
        void onOpenFaces();
        void onOpenMaps();
        void onOpenResources();
        void onOpenExperience();
        void onSaveFormulae();
        void browsingMap(const QString& path);
        void browsingFinished();
        void onFiltersModified();
        void onReportsModified();
};
